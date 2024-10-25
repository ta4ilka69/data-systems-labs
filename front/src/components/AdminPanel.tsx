// src/components/AdminPanel.tsx
import React, { useEffect, useState } from "react";
import {
  getAllAdminRoleRequests,
  approveAdminRoleRequest,
} from "../api/adminService";
import { UserDTO } from "../types";

const AdminPanel: React.FC = () => {
  const [users, setUsers] = useState<UserDTO[]>([]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const userList = await getAllAdminRoleRequests();
        setUsers(userList);
      } catch (err) {
        console.error("Failed to fetch users", err);
      }
    };
    fetchUsers();
  }, []);

  const handleApprove = async (userId: number) => {
    try {
      await approveAdminRoleRequest(userId);
      setUsers(
        users.map((user) =>
          user.id === userId ? { ...user, roles: ["ADMIN"] } : user
        )
      );
    } catch (err) {
      console.error("Failed to approve admin role", err);
    }
  };

  return (
    <div>
      <h2>Admin Panel</h2>
      <ul>
        {users.map((user) => (
          <li key={user.id}>
            {user.username} - Roles: {user.roles.join(", ")}
            {user.roles.includes("ADMIN") ? null : (
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
