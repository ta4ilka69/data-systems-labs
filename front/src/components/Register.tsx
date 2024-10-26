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
    if (password.length < 5) {
      setMessage("Password must be at least 5 characters long.");
      return;
    }
    const registerRequest: RegisterRequestDTO = { username, password };
    try {
      await registerUser(registerRequest);
      setMessage("Registration successful! Redirecting to login...");
      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (err: unknown) {
      if (
        err instanceof Error &&
        "response" in err &&
        typeof err.response === "object" &&
        err.response !== null
      ) {
        if (
          "data" in err.response &&
          typeof err.response.data === "object" &&
          err.response.data !== null &&
          "message" in err.response.data
        ) {
          setMessage(err.response.data.message as string);
        } else {
          setMessage("An error occurred during registration.");
        }
      } else {
        setMessage("An unexpected error occurred. Please try again.");
      }
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
