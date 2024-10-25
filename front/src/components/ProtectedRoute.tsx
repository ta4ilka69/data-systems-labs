import React from "react";
import { Navigate } from "react-router-dom";

interface ProtectedRouteProps {
  role?: string;
  children: JSX.Element;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ role, children }) => {
  const token = localStorage.getItem("token");
  const userRole = localStorage.getItem("role");

  if (!token || (role && userRole !== role)) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

export default ProtectedRoute;
