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
          {role && (
            <>
              <Link to="/admin" className="nav-link">
                Admin
              </Link>
              <Link to="/find-routes" className="nav-link">
                Find Routes
                </Link>
              <Link to="/get-routes-by-rating" className="nav-link">
                Get Routes By Rating
                </Link>
              <Link to="/import"
                className="nav-link"
              >
                Import
              </Link>
              <Link to="/import-history" className="nav-link">
                Import History
              </Link>
              <Link to="/delete-routes-by-rating"
                className="nav-link"
              >
                Delete Routes By Rating
              </Link>
              <Link to = "/map" className="nav-link">
                View Map
              </Link>
            </>
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
