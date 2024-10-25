import React, { useState } from "react";
import { registerUser } from "../api/authService";
import { RegisterRequestDTO } from "../types";

const Register: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  const handleRegister = async () => {
    const registerRequest: RegisterRequestDTO = { username, password };
    try {
      await registerUser(registerRequest);
      setMessage("Registration successful! Please log in.");
    } catch (err) {
      setMessage("Registration failed. Please try again.");
    }
  };

  return (
    <div>
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
      {message && <p>{message}</p>}
    </div>
  );
};

export default Register;
