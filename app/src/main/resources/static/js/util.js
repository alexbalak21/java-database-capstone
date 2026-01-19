// util.js
  function setRole(role) {
    localStorage.setItem("userRole", role);
  }
  
  function getRole() {
    return localStorage.getItem("userRole");
  }
  
  function clearRole() {
    localStorage.removeItem("userRole");
  }
  
  /**
   * Fetch wrapper that automatically adds JWT token to Authorization header
   * @param {string} url - The URL to fetch
   * @param {object} options - Fetch options (method, body, headers, etc.)
   * @returns {Promise} - The fetch response
   */
  function authorizedFetch(url, options = {}) {
    const token = localStorage.getItem("token");
    
    // Initialize headers if not provided
    if (!options.headers) {
      options.headers = {};
    }
    
    // Add authorization header if token exists
    if (token) {
      options.headers["Authorization"] = `Bearer ${token}`;
    }
    
    // Ensure Content-Type is set for JSON requests
    if (options.body && !options.headers["Content-Type"]) {
      options.headers["Content-Type"] = "application/json";
    }
    
    return fetch(url, options);
  }
  
