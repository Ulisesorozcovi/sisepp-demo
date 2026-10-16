import { useEffect, useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { planningApi } from '../api/planningApi';
import { getApiErrorMessage } from '../utils/apiError';
import type { PlanningInstrument, InstrumentType, CreatePlanningInstrumentRequest } from '../types';

const TYPE_LABELS: Record<InstrumentType, string> = {
  PGAR: 'Plan de Gestión Ambiental Regional - PGAR',
  PAC: 'Plan de Acción Cuatrienal - PAC',
  POAI: 'Plan Operativo Anual de Inversiones - POAI',
};

const APPROVAL_REF_LABELS: Record<InstrumentType, string> = {
  PGAR: 'Resolución de Aprobación',
  PAC: 'Acuerdo de Aprobación',
  POAI: 'Acuerdo de Aprobación',
};

const STATUS_LABELS: Record<string, string> = {
  ACTIVO: 'Activo',
  CERRADO: 'Cerrado',
  EN_FORMULACION: 'En Formulación',
};

const STATUS_COLORS: Record<string, string> = {
  ACTIVO: 'var(--green-700)',
  CERRADO: 'var(--ink-400)',
  EN_FORMULACION: 'var(--blue-600)',
};

export function PlanningInstrumentsPage() {
  const [instruments, setInstruments] = useState<PlanningInstrument[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [showForm, setShowForm] = useState(false);
  const [formType, setFormType] = useState<InstrumentType>('PGAR');
  const [formName, setFormName] = useState('');
  const [formStartYear, setFormStartYear] = useState(new Date().getFullYear());
  const [formEndYear, setFormEndYear] = useState(new Date().getFullYear() + 10);
  const [formParentId, setFormParentId] = useState('');
  const [formApprovalRef, setFormApprovalRef] = useState('');
  const [formApprovedDate, setFormApprovedDate] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadAll();
  }, []);

  async function loadAll() {
    setLoading(true);
    try {
      const data = await planningApi.list();
      setInstruments(data);
    } catch (e) {
      setError(getApiErrorMessage(e, 'Error al cargar instrumentos'));
    } finally {
      setLoading(false);
    }
  }

  function resetForm() {
    setFormName('');
    setFormStartYear(new Date().getFullYear());
    setFormEndYear(new Date().getFullYear() + 10);
    setFormParentId('');
    setFormApprovalRef('');
    setFormApprovedDate('');
    setShowForm(false);
  }

  function openForm(type: InstrumentType) {
    setFormType(type);
    if (type === 'PGAR') {
      setFormEndYear(new Date().getFullYear() + 10);
    } else if (type === 'PAC') {
      setFormEndYear(new Date().getFullYear() + 3);
    } else {
      setFormEndYear(new Date().getFullYear());
    }
    setShowForm(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      const payload: CreatePlanningInstrumentRequest = {
        type: formType,
        name: formName,
        startYear: formStartYear,
        endYear: formEndYear,
        approvalRef: formApprovalRef || undefined,
        approvedDate: formApprovedDate || undefined,
      };
      if (formParentId) payload.parentId = formParentId;

      await planningApi.create(payload);
      setSuccess(`${formType} creado exitosamente`);
      resetForm();
      await loadAll();
      setTimeout(() => setSuccess(''), 3000);
    } catch (e) {
      setError(getApiErrorMessage(e, 'Error al crear instrumento'));
    } finally {
      setSubmitting(false);
    }
  }

  const pgars = instruments.filter((i) => i.type === 'PGAR');
  const pacs = instruments.filter((i) => i.type === 'PAC');
  const poais = instruments.filter((i) => i.type === 'POAI');

  const parentOptions = formType === 'PAC' ? pgars : formType === 'POAI' ? pacs : [];

  return (
    <DashboardLayout>
      <div className="planning-page">
        <div className="planning-header">
          <h1>Instrumentos de Planificación</h1>
          <p className="planning-subtitle">
            Gestione la jerarquía de planificación: PGAR → PAC → POAI
          </p>
        </div>

        {error && <div className="alert-error">{error}</div>}
        {success && <div className="alert-success">{success}</div>}

        {loading ? (
          <p className="planning-loading">Cargando instrumentos…</p>
        ) : (
          <div className="planning-grid">
            {/* PGAR */}
            <section className="planning-section">
              <div className="planning-section-header">
                <div>
                  <h2 className="planning-section-title pgar">PGAR</h2>
                  <p className="planning-section-desc">Plan de Gestión Ambiental Regional</p>
                </div>
                <button className="btn-create" onClick={() => openForm('PGAR')}>+ Nuevo PGAR</button>
              </div>
              {pgars.length === 0 ? (
                <p className="planning-empty">No hay PGAR registrados</p>
              ) : (
                pgars.map((inst) => (
                  <InstrumentCard key={inst.id} instrument={inst} />
                ))
              )}
            </section>

            {/* PAC */}
            <section className="planning-section">
              <div className="planning-section-header">
                <div>
                  <h2 className="planning-section-title pac">PAC</h2>
                  <p className="planning-section-desc">Plan de Acción Cuatrienal</p>
                </div>
                <button className="btn-create" onClick={() => openForm('PAC')} disabled={pgars.length === 0}>
                  + Nuevo PAC
                </button>
              </div>
              {pacs.length === 0 ? (
                <p className="planning-empty">No hay PAC registrados</p>
              ) : (
                pacs.map((inst) => {
                  const parent = pgars.find((p) => p.id === inst.parentId);
                  return <InstrumentCard key={inst.id} instrument={inst} parentName={parent?.name} />;
                })
              )}
            </section>

            {/* POAI */}
            <section className="planning-section">
              <div className="planning-section-header">
                <div>
                  <h2 className="planning-section-title poai">POAI</h2>
                  <p className="planning-section-desc">Plan Operativo Anual de Inversiones</p>
                </div>
                <button className="btn-create" onClick={() => openForm('POAI')} disabled={pacs.length === 0}>
                  + Nuevo POAI
                </button>
              </div>
              {poais.length === 0 ? (
                <p className="planning-empty">No hay POAI registrados</p>
              ) : (
                poais.map((inst) => {
                  const parent = pacs.find((p) => p.id === inst.parentId);
                  return <InstrumentCard key={inst.id} instrument={inst} parentName={parent?.name} />;
                })
              )}
            </section>
          </div>
        )}

        {/* Modal de creación */}
        {showForm && (
          <div className="modal-backdrop" onClick={() => resetForm()}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h2>Crear {TYPE_LABELS[formType]}</h2>
              <form onSubmit={handleSubmit}>
                <div className="form-row">
                  <label className="form-label">
                    Nombre *
                    <input
                      className="atom-input"
                      value={formName}
                      onChange={(e) => setFormName(e.target.value)}
                      placeholder={`Nombre del ${formType}`}
                      required
                    />
                  </label>
                </div>

                <div className="form-row form-row--2col">
                  <label className="form-label">
                    Año Inicio *
                    <input
                      type="number"
                      className="atom-input"
                      value={formStartYear}
                      onChange={(e) => setFormStartYear(Number(e.target.value))}
                      min={2000}
                      max={2100}
                      required
                    />
                  </label>
                  <label className="form-label">
                    Año Fin *
                    <input
                      type="number"
                      className="atom-input"
                      value={formEndYear}
                      onChange={(e) => setFormEndYear(Number(e.target.value))}
                      min={formStartYear}
                      max={2100}
                      required
                    />
                  </label>
                </div>

                {parentOptions.length > 0 && (
                  <div className="form-row">
                    <label className="form-label">
                      {formType === 'PAC' ? 'PGAR padre' : 'PAC padre'} *
                      <select
                        className="atom-select"
                        value={formParentId}
                        onChange={(e) => setFormParentId(e.target.value)}
                        required
                      >
                        <option value="">-- Seleccione --</option>
                        {parentOptions.map((p) => (
                          <option key={p.id} value={p.id}>
                            {p.name} ({p.startYear}-{p.endYear})
                          </option>
                        ))}
                      </select>
                    </label>
                  </div>
                )}

                <div className="form-row form-row--2col">
                  <label className="form-label">
                    {APPROVAL_REF_LABELS[formType]}
                    <input
                      className="atom-input"
                      value={formApprovalRef}
                      onChange={(e) => setFormApprovalRef(e.target.value)}
                      placeholder={formType === 'PGAR' ? 'Ej: Resolución 001 de 2026' : 'Ej: Acuerdo 001 de 2026'}
                    />
                  </label>
                  <label className="form-label">
                    Fecha de Aprobación
                    <input
                      type="date"
                      className="atom-input"
                      value={formApprovedDate}
                      onChange={(e) => setFormApprovedDate(e.target.value)}
                    />
                  </label>
                </div>

                <div className="form-actions">
                  <button type="button" className="btn-secondary" onClick={resetForm}>
                    Cancelar
                  </button>
                  <button type="submit" className="btn-primary" disabled={submitting}>
                    {submitting ? 'Creando…' : 'Crear'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}

function InstrumentCard({ instrument, parentName }: { instrument: PlanningInstrument; parentName?: string }) {
  return (
    <div className="instrument-card">
      <div className="instrument-card-header">
        <h3 className="instrument-card-name">{instrument.name}</h3>
        <span
          className="instrument-status"
          style={{ color: STATUS_COLORS[instrument.status], borderColor: STATUS_COLORS[instrument.status] }}
        >
          {STATUS_LABELS[instrument.status]}
        </span>
      </div>
      <div className="instrument-card-meta">
        <span>Vigencia: {instrument.startYear} – {instrument.endYear}</span>
        {parentName && <span>Padre: {parentName}</span>}
        {instrument.approvalRef && (
          <span>{APPROVAL_REF_LABELS[instrument.type]}: {instrument.approvalRef}</span>
        )}
        {instrument.approvedDate && (
          <span>Fecha de Aprobación: {new Date(instrument.approvedDate + 'T00:00:00').toLocaleDateString('es-CO')}</span>
        )}
      </div>
    </div>
  );
}
