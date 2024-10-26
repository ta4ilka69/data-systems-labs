import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import Login from "./components/Login";
import Register from "./components/Register";
import RouteManager from "./components/RouteManager";
import AdminPanel from "./components/AdminPanel";
import ProtectedRoute from "./components/ProtectedRoute";
import Header from "./components/Header";
import FindRoutesBetweenLocations from "./components/FindRoutesBetweenLocations";
import GetRoutesByRatingLessThan from "./components/GetRoutesByRatingLessThan";
import DeleteRoutesByRating from "./components/DeleteRoutesByRating";
import MapView from "./components/MapView";

const App: React.FC = () => {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route
          path="/routes"
          element={
            <ProtectedRoute role={["USER", "ADMIN"]}>
              <RouteManager />
            </ProtectedRoute>
          }
        />
        <Route path="/routes" element={<RouteManager />} />
        <Route
          path="/find-routes"
          element={
            <ProtectedRoute role={["USER", "ADMIN"]}>
              <FindRoutesBetweenLocations />
            </ProtectedRoute>
          }
        />
        <Route
          path="/get-routes-by-rating"
          element={
            <ProtectedRoute role={["USER", "ADMIN"]}>
              <GetRoutesByRatingLessThan />
            </ProtectedRoute>
          }
        />
        <Route
          path="/delete-routes-by-rating"
          element={
            <ProtectedRoute role={["USER", "ADMIN"]}>
              <DeleteRoutesByRating />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <ProtectedRoute role={["USER", "ADMIN"]}>
              <AdminPanel />
            </ProtectedRoute>
          }
        />
        <Route
          path="/map"
          element={
            <ProtectedRoute role={["USER", "ADMIN"]}>
              <MapView />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
};

export default App;
