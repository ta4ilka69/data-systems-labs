import React, { useEffect, useState } from "react";
import {
  getAllAdminRoleRequests,
  approveAdminRoleRequest,
} from "../api/adminService";
import { UserDTO } from "../types";
import "./AdminPanel.css";

const AdminPanel: React.FC = () => {
  const [users, setUsers] = useState<UserDTO[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const userList = await getAllAdminRoleRequests();
        setUsers(userList);
      } catch (err) {
        console.error("Failed to fetch users", err);
        setError("Failed to fetch users.");
      }
    };
    fetchUsers();
  }, []);

  const handleApprove = async (userId: number) => {
    try {
      await approveAdminRoleRequest(userId);
      const updatedUsers = await getAllAdminRoleRequests();
      setUsers(updatedUsers);
    } catch (err) {
      console.error("Failed to approve admin role", err);
      setError("Failed to approve admin role.");
    }
  };

  return (
    <div className="admin-panel">
      <h2>Admin Panel</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <table className="admin-table">
        <thead>
          <tr>
            <th>Username</th>
            <th>Roles</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.username}</td>
              <td>{user.roles.join(", ")}</td>
              <td>
                {!user.roles.includes("ADMIN") && (
                  <button onClick={() => handleApprove(user.id)}>
                    Approve as Admin
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminPanel;
