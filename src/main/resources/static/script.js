/**
 * Goal Seek Engine - Frontend Application
 * Handles all UI interactions and API calls
 */

// ============================================
// Configuration
// ============================================

const API_BASE = '/api';
const ENDPOINTS = {
    formulas: `${API_BASE}/formulas`,
    goalSeek: `${API_BASE}/goal-seek`,
    evaluate: (name) => `${API_BASE}/goal-seek/evaluate/${encodeURIComponent(name)}`
};

// ============================================
// State Management
// ============================================

const state = {
    formulas: [],
    selectedFormula: null,
    evalSelectedFormula: null,
    editingFormula: null
};

// ============================================
// DOM Elements
// ============================================

const elements = {
    // Navigation
    navTabs: document.querySelectorAll('.nav-tab'),
    tabContents: document.querySelectorAll('.tab-content'),

    // Calculator Tab
    formulaSelect: document.getElementById('formula-select'),
    formulaInfo: document.getElementById('formula-info'),
    formulaExpression: document.getElementById('formula-expression'),
    formulaDescription: document.getElementById('formula-description'),
    variablesContainer: document.getElementById('variables-container'),
    seekVariable: document.getElementById('seek-variable'),
    targetValue: document.getElementById('target-value'),
    knownValuesInputs: document.getElementById('known-values-inputs'),
    lowerBound: document.getElementById('lower-bound'),
    upperBound: document.getElementById('upper-bound'),
    initialGuess: document.getElementById('initial-guess'),
    calculateBtn: document.getElementById('calculate-btn'),
    goalSeekForm: document.getElementById('goal-seek-form'),
    resultCard: document.getElementById('result-card'),
    resultValue: document.getElementById('result-value'),
    resultVariable: document.getElementById('result-variable'),
    resultTarget: document.getElementById('result-target'),
    resultAchieved: document.getElementById('result-achieved'),
    resultError: document.getElementById('result-error'),
    resultIterations: document.getElementById('result-iterations'),
    resultAlgorithm: document.getElementById('result-algorithm'),
    resultStatus: document.getElementById('result-status'),
    resultAllValues: document.getElementById('result-all-values'),

    // Formulas Tab
    formulaForm: document.getElementById('formula-form'),
    formulaFormTitle: document.getElementById('formula-form-title'),
    formulaSubmitText: document.getElementById('formula-submit-text'),
    editingFormulaName: document.getElementById('editing-formula-name'),
    newFormulaName: document.getElementById('new-formula-name'),
    newFormulaExpression: document.getElementById('new-formula-expression'),
    newFormulaDescription: document.getElementById('new-formula-description'),
    newFormulaOutput: document.getElementById('new-formula-output'),
    newFormulaVariables: document.getElementById('new-formula-variables'),
    cancelEditBtn: document.getElementById('cancel-edit-btn'),
    refreshFormulasBtn: document.getElementById('refresh-formulas-btn'),
    formulasList: document.getElementById('formulas-list'),

    // Evaluate Tab
    evalFormulaSelect: document.getElementById('eval-formula-select'),
    evalFormulaInfo: document.getElementById('eval-formula-info'),
    evalFormulaExpression: document.getElementById('eval-formula-expression'),
    evalVariablesContainer: document.getElementById('eval-variables-container'),
    evalValuesInputs: document.getElementById('eval-values-inputs'),
    evaluateBtn: document.getElementById('evaluate-btn'),
    evaluateForm: document.getElementById('evaluate-form'),
    evalResultCard: document.getElementById('eval-result-card'),
    evalResultValue: document.getElementById('eval-result-value'),
    evalResultFormula: document.getElementById('eval-result-formula'),
    evalInputSummary: document.getElementById('eval-input-summary'),

    // Toast
    toastContainer: document.getElementById('toast-container')
};

// ============================================
// API Functions
// ============================================

async function apiCall(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        if (!response.ok) {
            let errorData;
            try {
                errorData = await response.json();
            } catch {
                errorData = { message: `HTTP ${response.status}: ${response.statusText}` };
            }
            throw new Error(errorData.message || errorData.error || `Request failed with status ${response.status}`);
        }

        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

async function fetchFormulas() {
    try {
        state.formulas = await apiCall(ENDPOINTS.formulas);
        return state.formulas;
    } catch (error) {
        showToast('error', 'Failed to Load Formulas', error.message);
        return [];
    }
}

async function createFormula(data) {
    return await apiCall(ENDPOINTS.formulas, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

async function updateFormula(name, data) {
    return await apiCall(`${ENDPOINTS.formulas}/${encodeURIComponent(name)}`, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

async function deleteFormula(name) {
    return await apiCall(`${ENDPOINTS.formulas}/${encodeURIComponent(name)}`, {
        method: 'DELETE'
    });
}

async function performGoalSeek(data) {
    return await apiCall(ENDPOINTS.goalSeek, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

async function evaluateFormula(name, values) {
    return await apiCall(ENDPOINTS.evaluate(name), {
        method: 'POST',
        body: JSON.stringify(values)
    });
}

// ============================================
// UI Functions
// ============================================

function showToast(type, title, message) {
    const icons = {
        success: '✓',
        error: '✕',
        warning: '!'
    };

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <span class="toast-icon">${icons[type]}</span>
        <div class="toast-content">
            <div class="toast-title">${title}</div>
            ${message ? `<div class="toast-message">${message}</div>` : ''}
        </div>
        <button class="toast-close">×</button>
    `;

    elements.toastContainer.appendChild(toast);

    const closeBtn = toast.querySelector('.toast-close');
    closeBtn.addEventListener('click', () => removeToast(toast));

    setTimeout(() => removeToast(toast), 5000);
}

function removeToast(toast) {
    if (!toast.parentElement) return;
    toast.classList.add('removing');
    setTimeout(() => toast.remove(), 300);
}

function switchTab(tabId) {
    elements.navTabs.forEach(tab => {
        tab.classList.toggle('active', tab.dataset.tab === tabId);
    });

    elements.tabContents.forEach(content => {
        content.classList.toggle('active', content.id === `${tabId}-tab`);
    });
}

function populateFormulaSelects() {
    const options = state.formulas.map(f =>
        `<option value="${f.name}">${f.name}</option>`
    ).join('');

    const defaultOption = '<option value="">Select a formula...</option>';

    elements.formulaSelect.innerHTML = defaultOption + options;
    elements.evalFormulaSelect.innerHTML = defaultOption + options;
}

function renderFormulasList() {
    if (state.formulas.length === 0) {
        elements.formulasList.innerHTML = `
            <div class="empty-state">
                <p>No formulas found. Create one to get started!</p>
            </div>
        `;
        return;
    }

    elements.formulasList.innerHTML = state.formulas.map(formula => `
        <div class="formula-item" data-name="${formula.name}">
            <div class="formula-item-header">
                <span class="formula-item-name">${formula.name}</span>
                <div class="formula-item-actions">
                    <button class="edit-btn" title="Edit" onclick="editFormula('${formula.name}')">✎</button>
                    <button class="delete-btn" title="Delete" onclick="confirmDeleteFormula('${formula.name}')">×</button>
                </div>
            </div>
            <div class="formula-item-expression">${formula.expression}</div>
            <div class="formula-item-meta">
                <span class="meta-item">Output: ${formula.outputVariable}</span>
                <span class="meta-item">Variables: ${formula.variables.join(', ')}</span>
            </div>
            ${formula.description ? `<div class="info-desc" style="margin-top: 8px;">${formula.description}</div>` : ''}
        </div>
    `).join('');
}

function setupCalculatorForFormula(formula) {
    state.selectedFormula = formula;

    // Show formula info
    elements.formulaInfo.classList.remove('hidden');
    elements.formulaExpression.textContent = formula.expression;
    elements.formulaDescription.textContent = formula.description || '';

    // Show variables container
    elements.variablesContainer.classList.remove('hidden');

    // Populate seek variable dropdown
    elements.seekVariable.innerHTML = '<option value="">Select variable...</option>' +
        formula.variables.map(v => `<option value="${v}">${v}</option>`).join('');

    // Clear known values
    elements.knownValuesInputs.innerHTML = '';

    // Enable calculate button
    updateCalculateButton();
}

function updateKnownValuesInputs() {
    const seekVar = elements.seekVariable.value;
    const formula = state.selectedFormula;

    if (!formula) return;

    const otherVars = formula.variables.filter(v => v !== seekVar);

    elements.knownValuesInputs.innerHTML = otherVars.map(v => `
        <div class="variable-input">
            <label for="var-${v}">${v}</label>
            <input type="number" id="var-${v}" step="any" data-variable="${v}" required placeholder="Enter value">
        </div>
    `).join('');
}

function updateCalculateButton() {
    const hasFormula = elements.formulaSelect.value !== '';
    const hasSeekVar = elements.seekVariable.value !== '';
    const hasTarget = elements.targetValue.value !== '';

    elements.calculateBtn.disabled = !(hasFormula && hasSeekVar && hasTarget);
}

function setupEvaluatorForFormula(formula) {
    state.evalSelectedFormula = formula;

    // Show formula info
    elements.evalFormulaInfo.classList.remove('hidden');
    elements.evalFormulaExpression.textContent = formula.expression;

    // Show variables container
    elements.evalVariablesContainer.classList.remove('hidden');

    // Create inputs for all variables
    elements.evalValuesInputs.innerHTML = formula.variables.map(v => `
        <div class="variable-input">
            <label for="eval-var-${v}">${v}</label>
            <input type="number" id="eval-var-${v}" step="any" data-variable="${v}" required placeholder="Enter value">
        </div>
    `).join('');

    elements.evaluateBtn.disabled = false;
}

function displayGoalSeekResult(result) {
    elements.resultCard.classList.remove('hidden');

    elements.resultValue.textContent = formatNumber(result.computedValue);
    elements.resultVariable.textContent = result.seekVariable;
    elements.resultTarget.textContent = formatNumber(result.targetValue);
    elements.resultAchieved.textContent = formatNumber(result.achievedValue);
    elements.resultError.textContent = formatScientific(result.error);
    elements.resultIterations.textContent = result.iterations;
    elements.resultAlgorithm.textContent = result.algorithm;

    const statusBadge = result.success
        ? '<span class="status-badge success">✓ Success</span>'
        : '<span class="status-badge error">✕ Failed</span>';
    elements.resultStatus.innerHTML = statusBadge;

    // Display all values
    if (result.allValues) {
        const valuesHtml = Object.entries(result.allValues)
            .map(([key, val]) => `
                <span class="value-chip">
                    <span class="var-name">${key}</span> =
                    <span class="var-value">${formatNumber(val)}</span>
                </span>
            `).join('');

        elements.resultAllValues.innerHTML = `
            <h4>All Variable Values</h4>
            <div class="all-values-list">${valuesHtml}</div>
        `;
    }
}

function displayEvaluationResult(result, inputValues) {
    elements.evalResultCard.classList.remove('hidden');

    elements.evalResultValue.textContent = formatNumber(result.result);
    elements.evalResultFormula.textContent = result.formulaName;

    const inputsHtml = Object.entries(inputValues)
        .map(([key, val]) => `
            <span class="value-chip">
                <span class="var-name">${key}</span> =
                <span class="var-value">${formatNumber(val)}</span>
            </span>
        `).join('');

    elements.evalInputSummary.innerHTML = `
        <h4>Input Values</h4>
        <div class="all-values-list">${inputsHtml}</div>
    `;
}

function formatNumber(num) {
    if (num === null || num === undefined) return '—';
    if (Math.abs(num) >= 1e6 || (Math.abs(num) < 0.001 && num !== 0)) {
        return num.toExponential(4);
    }
    return num.toLocaleString('en-US', { maximumFractionDigits: 6 });
}

function formatScientific(num) {
    if (num === null || num === undefined) return '—';
    return num.toExponential(2);
}

function resetFormulaForm() {
    elements.formulaForm.reset();
    elements.editingFormulaName.value = '';
    elements.formulaFormTitle.textContent = 'Create Formula';
    elements.formulaSubmitText.textContent = 'Create Formula';
    elements.cancelEditBtn.style.display = 'none';
    elements.newFormulaName.disabled = false;
    state.editingFormula = null;
}

// ============================================
// Event Handlers
// ============================================

function editFormula(name) {
    const formula = state.formulas.find(f => f.name === name);
    if (!formula) return;

    state.editingFormula = formula;

    elements.formulaFormTitle.textContent = 'Edit Formula';
    elements.formulaSubmitText.textContent = 'Update Formula';
    elements.cancelEditBtn.style.display = 'inline-flex';
    elements.editingFormulaName.value = name;
    elements.newFormulaName.value = formula.name;
    elements.newFormulaName.disabled = true; // Can't change name during edit
    elements.newFormulaExpression.value = formula.expression;
    elements.newFormulaDescription.value = formula.description || '';
    elements.newFormulaOutput.value = formula.outputVariable;
    elements.newFormulaVariables.value = formula.variables.join(', ');

    // Scroll to form
    elements.formulaForm.scrollIntoView({ behavior: 'smooth' });
}

async function confirmDeleteFormula(name) {
    if (!confirm(`Are you sure you want to delete the formula "${name}"?`)) {
        return;
    }

    try {
        await deleteFormula(name);
        showToast('success', 'Formula Deleted', `"${name}" has been removed.`);
        await loadFormulas();
    } catch (error) {
        showToast('error', 'Delete Failed', error.message);
    }
}

// Make these available globally for onclick handlers
window.editFormula = editFormula;
window.confirmDeleteFormula = confirmDeleteFormula;

// ============================================
// Initialization
// ============================================

async function loadFormulas() {
    await fetchFormulas();
    populateFormulaSelects();
    renderFormulasList();
}

function initEventListeners() {
    // Navigation
    elements.navTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            switchTab(tab.dataset.tab);
        });
    });

    // Calculator - Formula Selection
    elements.formulaSelect.addEventListener('change', async (e) => {
        const name = e.target.value;
        if (!name) {
            elements.formulaInfo.classList.add('hidden');
            elements.variablesContainer.classList.add('hidden');
            elements.resultCard.classList.add('hidden');
            state.selectedFormula = null;
            updateCalculateButton();
            return;
        }

        const formula = state.formulas.find(f => f.name === name);
        if (formula) {
            setupCalculatorForFormula(formula);
        }
    });

    // Calculator - Seek Variable Change
    elements.seekVariable.addEventListener('change', () => {
        updateKnownValuesInputs();
        updateCalculateButton();
    });

    // Calculator - Target Value Change
    elements.targetValue.addEventListener('input', updateCalculateButton);

    // Calculator - Form Submit
    elements.goalSeekForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formulaName = elements.formulaSelect.value;
        const seekVariable = elements.seekVariable.value;
        const targetValue = parseFloat(elements.targetValue.value);

        // Collect known values
        const knownValues = {};
        document.querySelectorAll('#known-values-inputs input').forEach(input => {
            const varName = input.dataset.variable;
            const value = parseFloat(input.value);
            if (!isNaN(value)) {
                knownValues[varName] = value;
            }
        });

        const request = {
            formulaName,
            seekVariable,
            targetValue,
            knownValues
        };

        // Optional bounds
        const lowerBound = elements.lowerBound.value;
        const upperBound = elements.upperBound.value;
        const initialGuess = elements.initialGuess.value;

        if (lowerBound) request.lowerBound = parseFloat(lowerBound);
        if (upperBound) request.upperBound = parseFloat(upperBound);
        if (initialGuess) request.initialGuess = parseFloat(initialGuess);

        elements.calculateBtn.disabled = true;
        elements.calculateBtn.innerHTML = '<span class="spinner"></span> Calculating...';

        try {
            const result = await performGoalSeek(request);
            displayGoalSeekResult(result);

            if (result.success) {
                showToast('success', 'Goal Seek Complete', `Found ${seekVariable} = ${formatNumber(result.computedValue)}`);
            } else {
                showToast('warning', 'Goal Seek Completed', result.message || 'Solution may not be optimal');
            }
        } catch (error) {
            showToast('error', 'Goal Seek Failed', error.message);
            elements.resultCard.classList.add('hidden');
        } finally {
            elements.calculateBtn.disabled = false;
            elements.calculateBtn.innerHTML = '<span class="btn-icon">⟩</span> Calculate';
            updateCalculateButton();
        }
    });

    // Formulas - Form Submit
    elements.formulaForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const name = elements.newFormulaName.value.trim();
        const expression = elements.newFormulaExpression.value.trim();
        const description = elements.newFormulaDescription.value.trim();
        const outputVariable = elements.newFormulaOutput.value.trim();
        const variables = elements.newFormulaVariables.value
            .split(',')
            .map(v => v.trim())
            .filter(v => v);

        if (variables.length === 0) {
            showToast('error', 'Validation Error', 'At least one variable is required');
            return;
        }

        const data = { name, expression, description, outputVariable, variables };

        try {
            if (state.editingFormula) {
                await updateFormula(state.editingFormula.name, data);
                showToast('success', 'Formula Updated', `"${name}" has been updated.`);
            } else {
                await createFormula(data);
                showToast('success', 'Formula Created', `"${name}" has been added.`);
            }

            resetFormulaForm();
            await loadFormulas();
        } catch (error) {
            showToast('error', state.editingFormula ? 'Update Failed' : 'Creation Failed', error.message);
        }
    });

    // Formulas - Cancel Edit
    elements.cancelEditBtn.addEventListener('click', resetFormulaForm);

    // Formulas - Refresh
    elements.refreshFormulasBtn.addEventListener('click', async () => {
        elements.formulasList.innerHTML = '<div class="loading-state">Loading formulas...</div>';
        await loadFormulas();
        showToast('success', 'Refreshed', 'Formula list updated');
    });

    // Evaluate - Formula Selection
    elements.evalFormulaSelect.addEventListener('change', (e) => {
        const name = e.target.value;
        if (!name) {
            elements.evalFormulaInfo.classList.add('hidden');
            elements.evalVariablesContainer.classList.add('hidden');
            elements.evalResultCard.classList.add('hidden');
            elements.evaluateBtn.disabled = true;
            state.evalSelectedFormula = null;
            return;
        }

        const formula = state.formulas.find(f => f.name === name);
        if (formula) {
            setupEvaluatorForFormula(formula);
        }
    });

    // Evaluate - Form Submit
    elements.evaluateForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formulaName = elements.evalFormulaSelect.value;

        // Collect all values
        const values = {};
        document.querySelectorAll('#eval-values-inputs input').forEach(input => {
            const varName = input.dataset.variable;
            const value = parseFloat(input.value);
            if (!isNaN(value)) {
                values[varName] = value;
            }
        });

        elements.evaluateBtn.disabled = true;
        elements.evaluateBtn.innerHTML = '<span class="spinner"></span> Evaluating...';

        try {
            const result = await evaluateFormula(formulaName, values);
            displayEvaluationResult(result, values);
            showToast('success', 'Evaluation Complete', `Result: ${formatNumber(result.result)}`);
        } catch (error) {
            showToast('error', 'Evaluation Failed', error.message);
            elements.evalResultCard.classList.add('hidden');
        } finally {
            elements.evaluateBtn.disabled = false;
            elements.evaluateBtn.innerHTML = '<span class="btn-icon">▷</span> Evaluate';
        }
    });
}

// ============================================
// Bootstrap
// ============================================

document.addEventListener('DOMContentLoaded', async () => {
    initEventListeners();
    await loadFormulas();
});

