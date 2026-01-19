// render.js

function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');
  if (role === "admin") {
    window.location.href = "/adminDashboard";
  } else if (role === "patient") {
    // No token, redirect to patient login page
    window.location.href = "/pages/patientDashboard.html";
  } else if (role === "doctor") {
    window.location.href = "/doctorDashboard";
  }
}


function renderContent() {
  const role = getRole();
  if (!role) {
    window.location.href = "/"; // if no role, send to role selection page
    return;
  }
}
