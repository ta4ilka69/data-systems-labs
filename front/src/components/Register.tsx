import React, { useState } from "react";
import { registerUser } from "../api/authService";
import { RegisterRequestDTO } from "../types";
import { useNavigate, Link } from "react-router-dom";
import "./Auth.css";

const Register: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleRegister = async () => {
    const registerRequest: RegisterRequestDTO = { username, password };
    try {
      await registerUser(registerRequest);
      setMessage("Registration successful! Redirecting to login...");
      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (err) {
      setMessage("Registration failed. Please try again.");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Register</h2>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Username"
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
        />
        <button onClick={handleRegister}>Register</button>
        {message && (
          <p
            className={
              message.includes("successful")
                ? "success-message"
                : "error-message"
            }
          >
            {message}
          </p>
        )}
        <p>
          Already have an account?{" "}
          <Link to="/login" className="toggle-link">
            Login here
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
