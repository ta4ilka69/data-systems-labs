import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Header.css";
import { requestAdminRole } from "../api/authService";

const Header: React.FC = () => {
  const username = localStorage.getItem("username");
  const role = localStorage.getItem("role");
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    username && (
      <header className="header">
        <nav className="nav">
          <Link to="/routes" className="nav-link">
            Routes
          </Link>
          {role === "ADMIN" && (
            <Link to="/admin" className="nav-link">
              Admin
            </Link>
          )}
          {role === "USER" &&
            localStorage.getItem("adminRoleRequested") !== "true" && (
              <button
                onClick={requestAdminRole}
                className="request-admin-button"
              >
                Request Admin Role
              </button>
            )}
        </nav>
        <div className="user-info">
          {username ? (
            <>
              <span>Welcome, {username}</span>
              <button onClick={handleLogout} className="logout-button">
                Logout
              </button>
            </>
          ) : (
            <span>Not Logged In</span>
          )}
        </div>
      </header>
    )
  );
};

export default Header;
