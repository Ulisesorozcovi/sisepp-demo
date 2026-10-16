import { Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './routes/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { ResetPasswordPage } from './pages/ResetPasswordPage';
import { HomePage } from './pages/HomePage';
import { PlanningInstrumentsPage } from './pages/PlanningInstrumentsPage';
import { UsersPage } from './pages/UsersPage';
import { StructurePage } from './pages/StructurePage';
import { TaskCatalogPage } from './pages/TaskCatalogPage';
import { SchedulePage } from './pages/SchedulePage';
import { TasksPage } from './pages/TasksPage';
import { MyTasksPage } from './pages/MyTasksPage';
import { FollowUpsPage } from './pages/FollowUpsPage';
import { FinancialPage } from './pages/FinancialPage';
import { ReportsPage } from './pages/ReportsPage';
import { FollowUpFormPage } from './pages/FollowUpFormPage';
import { GoalTrackingPage } from './pages/GoalTrackingPage';
import { ConsolidatedReportPage } from './pages/ConsolidatedReportPage';
import { TaskQueryPage } from './pages/TaskQueryPage';

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />

        <Route
          path="/"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF', 'COORDINADOR', 'TECNICO']}>
              <HomePage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/instrumentos"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF']}>
              <PlanningInstrumentsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/usuarios"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF']}>
              <UsersPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/estructura"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF', 'COORDINADOR']}>
              <StructurePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/catalogo-tareas"
          element={
            <ProtectedRoute allowedRoles={['COORDINADOR', 'DIRECTOR', 'STAFF']}>
              <TaskCatalogPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/cronograma"
          element={
            <ProtectedRoute allowedRoles={['COORDINADOR', 'DIRECTOR', 'STAFF']}>
              <SchedulePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/tareas"
          element={
            <ProtectedRoute allowedRoles={['COORDINADOR', 'DIRECTOR', 'STAFF']}>
              <TasksPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/mis-tareas"
          element={
            <ProtectedRoute allowedRoles={['TECNICO']}>
              <MyTasksPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/seguimiento"
          element={
            <ProtectedRoute allowedRoles={['COORDINADOR', 'DIRECTOR', 'STAFF']}>
              <FollowUpsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/registrar-avance"
          element={
            <ProtectedRoute allowedRoles={['COORDINADOR', 'DIRECTOR', 'STAFF', 'TECNICO']}>
              <FollowUpFormPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/consulta-metas"
          element={
            <ProtectedRoute allowedRoles={['COORDINADOR', 'DIRECTOR', 'STAFF']}>
              <GoalTrackingPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/financiero"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF', 'COORDINADOR']}>
              <FinancialPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/reportes"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF', 'COORDINADOR']}>
              <ReportsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/reporte-consolidado"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF', 'COORDINADOR']}>
              <ConsolidatedReportPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/consulta-tareas"
          element={
            <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF', 'COORDINADOR']}>
              <TaskQueryPage />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={
          <ProtectedRoute allowedRoles={['DIRECTOR', 'STAFF', 'COORDINADOR', 'TECNICO']}>
            <HomePage />
          </ProtectedRoute>
        } />
      </Routes>
    </AuthProvider>
  );
}
