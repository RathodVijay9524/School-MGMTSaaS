/**
 * School Management System - Main JavaScript File
 * Common utilities and functions
 */

// API Configuration
const API_CONFIG = {
    baseURL: window.location.origin,
    endpoints: {
        login: '/api/auth/login',
        register: '/api/auth/register/admin',
        currentUser: '/api/auth/current-user',
        refreshToken: '/api/auth/refresh-token',
        logout: '/api/auth/logout'
    }
};

// Utility Functions
const Utils = {
    /**
     * Get stored JWT token
     */
    getToken() {
        return localStorage.getItem('jwtToken');
    },

    /**
     * Get stored refresh token
     */
    getRefreshToken() {
        return localStorage.getItem('refreshToken');
    },

    /**
     * Get stored user data
     */
    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    /**
     * Store authentication data
     */
    setAuthData(jwtToken, refreshToken, user) {
        localStorage.setItem('jwtToken', jwtToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
    },

    /**
     * Clear authentication data
     */
    clearAuthData() {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
    },

    /**
     * Check if user is authenticated
     */
    isAuthenticated() {
        return !!this.getToken();
    },

    /**
     * Get API headers with authentication
     */
    getAuthHeaders() {
        const token = this.getToken();
        return {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        };
    },

    /**
     * Show loading state for element
     */
    showLoading(element, loadingText = 'Loading...') {
        if (element) {
            element.disabled = true;
            element.innerHTML = `<i class="fas fa-spinner fa-spin mr-2"></i>${loadingText}`;
        }
    },

    /**
     * Hide loading state for element
     */
    hideLoading(element, originalText) {
        if (element) {
            element.disabled = false;
            element.innerHTML = originalText;
        }
    },

    /**
     * Show alert message
     */
    showAlert(message, type = 'info', containerId = 'messageContainer') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const alertClass = {
            success: 'bg-green-100 border-green-400 text-green-700',
            error: 'bg-red-100 border-red-400 text-red-700',
            warning: 'bg-yellow-100 border-yellow-400 text-yellow-700',
            info: 'bg-blue-100 border-blue-400 text-blue-700'
        };

        const iconClass = {
            success: 'fas fa-check-circle',
            error: 'fas fa-exclamation-circle',
            warning: 'fas fa-exclamation-triangle',
            info: 'fas fa-info-circle'
        };

        const alertHtml = `
            <div class="alert-auto-hide ${alertClass[type]} px-4 py-3 rounded mb-4 border">
                <div class="flex items-center">
                    <i class="${iconClass[type]} mr-2"></i>
                    <span>${message}</span>
                </div>
            </div>
        `;

        container.innerHTML = alertHtml;
        container.classList.remove('hidden');

        // Auto-hide after 5 seconds
        setTimeout(() => {
            const alert = container.querySelector('.alert-auto-hide');
            if (alert) {
                alert.style.opacity = '0';
                setTimeout(() => {
                    container.classList.add('hidden');
                }, 300);
            }
        }, 5000);
    },

    /**
     * Hide alert message
     */
    hideAlert(containerId = 'messageContainer') {
        const container = document.getElementById(containerId);
        if (container) {
            container.classList.add('hidden');
        }
    },

    /**
     * Format date
     */
    formatDate(date, options = {}) {
        const defaultOptions = {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        };
        return new Date(date).toLocaleDateString('en-US', { ...defaultOptions, ...options });
    },

    /**
     * Format currency
     */
    formatCurrency(amount, currency = 'USD') {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currency
        }).format(amount);
    },

    /**
     * Debounce function
     */
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    /**
     * Validate email
     */
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    /**
     * Validate password strength
     */
    validatePassword(password) {
        const minLength = 8;
        const hasUpperCase = /[A-Z]/.test(password);
        const hasLowerCase = /[a-z]/.test(password);
        const hasNumbers = /\d/.test(password);
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);

        return {
            isValid: password.length >= minLength && hasUpperCase && hasLowerCase && hasNumbers,
            strength: {
                length: password.length >= minLength,
                uppercase: hasUpperCase,
                lowercase: hasLowerCase,
                numbers: hasNumbers,
                special: hasSpecialChar
            }
        };
    }
};

// API Functions
const API = {
    /**
     * Make authenticated API request
     */
    async request(endpoint, options = {}) {
        const url = endpoint.startsWith('http') ? endpoint : `${API_CONFIG.baseURL}${endpoint}`;
        const config = {
            headers: Utils.getAuthHeaders(),
            ...options
        };

        try {
            const response = await fetch(url, config);
            
            if (response.status === 401) {
                // Token expired, try to refresh
                const refreshed = await this.refreshToken();
                if (refreshed) {
                    // Retry with new token
                    config.headers = Utils.getAuthHeaders();
                    return await fetch(url, config);
                } else {
                    // Refresh failed, redirect to login
                    Utils.clearAuthData();
                    window.location.href = '/login';
                    return;
                }
            }

            return response;
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    },

    /**
     * Refresh authentication token
     */
    async refreshToken() {
        const refreshToken = Utils.getRefreshToken();
        if (!refreshToken) return false;

        try {
            const response = await fetch(`${API_CONFIG.baseURL}${API_CONFIG.endpoints.refreshToken}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken })
            });

            if (response.ok) {
                const result = await response.json();
                if (result.responseStatus === 'OK') {
                    Utils.setAuthData(
                        result.data.jwtToken,
                        result.data.refreshToken,
                        result.data.user
                    );
                    return true;
                }
            }
            return false;
        } catch (error) {
            console.error('Token refresh failed:', error);
            return false;
        }
    },

    /**
     * Login user
     */
    async login(usernameOrEmail, password) {
        const response = await fetch(`${API_CONFIG.baseURL}${API_CONFIG.endpoints.login}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usernameOrEmail, password })
        });

        const result = await response.json();
        if (response.ok && result.responseStatus === 'OK') {
            Utils.setAuthData(
                result.data.jwtToken,
                result.data.refreshToken,
                result.data.user
            );
            return result;
        } else {
            throw new Error(result.message || 'Login failed');
        }
    },

    /**
     * Register user
     */
    async register(userData) {
        const response = await fetch(`${API_CONFIG.baseURL}${API_CONFIG.endpoints.register}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const result = await response.json();
        if (response.ok && result.responseStatus === 'OK') {
            return result;
        } else {
            throw new Error(result.message || 'Registration failed');
        }
    },

    /**
     * Logout user
     */
    async logout() {
        try {
            const refreshToken = Utils.getRefreshToken();
            if (refreshToken) {
                await fetch(`${API_CONFIG.baseURL}${API_CONFIG.endpoints.logout}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ refreshToken })
                });
            }
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            Utils.clearAuthData();
            window.location.href = '/login';
        }
    },

    /**
     * Get current user data
     */
    async getCurrentUser() {
        const response = await this.request(API_CONFIG.endpoints.currentUser);
        if (response.ok) {
            return await response.json();
        }
        throw new Error('Failed to get current user');
    }
};

// Navigation Functions
const Navigation = {
    /**
     * Redirect to role-based dashboard
     */
    redirectToDashboard(userRoles) {
        if (!userRoles || userRoles.length === 0) {
            window.location.href = '/dashboard';
            return;
        }

        const roleNames = userRoles.map(role => role.name);
        
        if (roleNames.includes('ROLE_ADMIN') || roleNames.includes('ROLE_SUPER_USER')) {
            window.location.href = '/dashboard/owner';
        } else if (roleNames.includes('ROLE_TEACHER')) {
            window.location.href = '/dashboard/teacher';
        } else if (roleNames.includes('ROLE_STUDENT')) {
            window.location.href = '/dashboard/student';
        } else if (roleNames.includes('ROLE_PARENT')) {
            window.location.href = '/dashboard/parent';
        } else {
            window.location.href = '/dashboard';
        }
    },

    /**
     * Check authentication and redirect if needed
     */
    requireAuth(redirectTo = '/login') {
        if (!Utils.isAuthenticated()) {
            window.location.href = redirectTo;
            return false;
        }
        return true;
    }
};

// Form Utilities
const FormUtils = {
    /**
     * Serialize form data to object
     */
    serializeForm(form) {
        const formData = new FormData(form);
        const data = {};
        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }
        return data;
    },

    /**
     * Validate form fields
     */
    validateForm(form, rules) {
        const errors = {};
        const formData = this.serializeForm(form);

        for (const field in rules) {
            const rule = rules[field];
            const value = formData[field];

            if (rule.required && (!value || value.trim() === '')) {
                errors[field] = `${field} is required`;
            } else if (rule.minLength && value && value.length < rule.minLength) {
                errors[field] = `${field} must be at least ${rule.minLength} characters`;
            } else if (rule.email && value && !Utils.isValidEmail(value)) {
                errors[field] = `${field} must be a valid email`;
            }
        }

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    },

    /**
     * Show form errors
     */
    showFormErrors(form, errors) {
        // Clear previous errors
        form.querySelectorAll('.error-message').forEach(el => el.remove());
        form.querySelectorAll('.border-red-500').forEach(el => {
            el.classList.remove('border-red-500');
            el.classList.add('border-gray-300');
        });

        // Show new errors
        for (const field in errors) {
            const input = form.querySelector(`[name="${field}"]`);
            if (input) {
                input.classList.add('border-red-500');
                input.classList.remove('border-gray-300');
                
                const errorDiv = document.createElement('div');
                errorDiv.className = 'error-message text-red-500 text-sm mt-1';
                errorDiv.textContent = errors[field];
                input.parentNode.appendChild(errorDiv);
            }
        }
    }
};

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Add fade-in animation to main content
    const mainContent = document.querySelector('main');
    if (mainContent) {
        mainContent.classList.add('fade-in');
    }

    // Initialize mobile menu
    const mobileMenuButton = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');
    
    if (mobileMenuButton && mobileMenu) {
        mobileMenuButton.addEventListener('click', function() {
            mobileMenu.classList.toggle('hidden');
        });

        // Close mobile menu when clicking outside
        document.addEventListener('click', function(event) {
            if (!mobileMenu.contains(event.target) && !mobileMenuButton.contains(event.target)) {
                mobileMenu.classList.add('hidden');
            }
        });
    }

    // Initialize user menu
    const userMenuButton = document.getElementById('userMenuButton');
    const userMenu = document.getElementById('userMenu');
    
    if (userMenuButton && userMenu) {
        userMenuButton.addEventListener('click', function() {
            userMenu.classList.toggle('hidden');
        });

        // Close user menu when clicking outside
        document.addEventListener('click', function(event) {
            if (!userMenu.contains(event.target) && !userMenuButton.contains(event.target)) {
                userMenu.classList.add('hidden');
            }
        });
    }

    // Initialize logout functionality
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', function() {
            API.logout();
        });
    }
});

// Export utilities for global access
window.Utils = Utils;
window.API = API;
window.Navigation = Navigation;
window.FormUtils = FormUtils;
