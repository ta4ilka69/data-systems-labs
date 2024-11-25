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
              <button
                onClick={() => navigate("/find-routes")}
                className="nav-link"
              >
                Find Routes
              </button>
              <button
                onClick={() => navigate("/get-routes-by-rating")}
                className="nav-link"
              >
                Get Routes By Rating
              </button>
              <button
                onClick={() => navigate("/import")}
                className="nav-link"
              >
                Import
              </button>
              <button
                onClick={() => navigate("/import-history")}
                className="nav-link"
              >
                Import History
              </button>
              <button
                onClick={() => navigate("/delete-routes-by-rating")}
                className="nav-link"
              >
                Delete Routes By Rating
              </button>
              <button onClick={() => navigate("/map")} className="nav-link">
                View Map
              </button>
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
