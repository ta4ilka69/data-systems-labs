import React from "react";

const UserInfo: React.FC = () => {
  const username = localStorage.getItem("username");
  const role = localStorage.getItem("role");

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    localStorage.removeItem("userId");
    window.location.reload();
  };

  return (
    <div className="user-info">
      {username ? (
        <div>
          <span>
            Logged in as: {username} ({role})
          </span>
          <button onClick={handleLogout}>Logout</button>
        </div>
      ) : (
        <span>Not Logged In</span>
      )}
    </div>
  );
};

export default UserInfo;
