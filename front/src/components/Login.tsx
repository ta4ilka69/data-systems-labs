import React, { useEffect, useState } from "react";
import { getUser, loginUser } from "../api/authService";
import { AuthRequestDTO} from "../types";
import { useNavigate, Link } from "react-router-dom";
import "./Auth.css";

const Login: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (localStorage.getItem("token") !== null) {
      navigate("/routes");
    }
  }, [navigate]);

  const handleLogin = async () => {
    const authRequest: AuthRequestDTO = { username, password };
    try {
      const { token } = await loginUser(authRequest);
      localStorage.setItem("token", token);
      await getUser();
      setError(null);
      navigate("/routes");
    } catch (err) {
      console.log(err);
      setError("Login failed. Check your username and password.");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Login</h2>
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
        <button onClick={handleLogin}>Login</button>
        {error && <p className="error-message">{error}</p>}
        <p>
          Don't have an account?{" "}
          <Link to="/register" className="toggle-link">
            Register here
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;