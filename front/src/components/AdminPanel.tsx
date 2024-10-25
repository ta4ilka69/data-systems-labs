import React, { useEffect, useState } from "react";
import {
  getAllAdminRoleRequests,
  approveAdminRoleRequest,
} from "../api/adminService";
import { UserDTO } from "../types";

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
      // Re-fetch the user list to reflect changes from the backend
      const updatedUsers = await getAllAdminRoleRequests();
      setUsers(updatedUsers);
    } catch (err) {
      console.error("Failed to approve admin role", err);
      setError("Failed to approve admin role.");
    }
  };

  return (
    <div>
      <h2>Admin Panel</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <ul>
        {users.map((user) => (
          <li key={user.id}>
            {user.username} - Roles: {user.roles.join(", ")}
            {!user.roles.includes("ADMIN") && (
              <button onClick={() => handleApprove(user.id)}>
                Approve as Admin
              </button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AdminPanel;
