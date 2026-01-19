// footer.js - Reusable Footer Component

/**
 * Renders the footer component
 * Static footer that appears on all pages
 */
function renderFooter() {
  const footer = document.getElementById("footer");
  
  footer.innerHTML = `
    <footer class="footer">
      <div class="footer-container">
        <!-- Logo and Copyright Section -->
        <div class="footer-logo">
          <img src="../assets/images/logo/Logo.png" alt="Hospital CMS Logo" class="footer-logo-img">
          <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
        </div>
        
        <!-- Links Section -->
        <div class="footer-links">
          <!-- Company Links Column -->
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
            <a href="#">Press</a>
          </div>
          
          <!-- Support Links Column -->
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Account</a>
            <a href="#">Help Center</a>
            <a href="#">Contact Us</a>
          </div>
          
          <!-- Legals Links Column -->
          <div class="footer-column">
            <h4>Legals</h4>
            <a href="#">Terms & Conditions</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Licensing</a>
          </div>
        </div>
      </div>
    </footer>
  `;
}

// Call the function to render footer when page loads
renderFooter();
