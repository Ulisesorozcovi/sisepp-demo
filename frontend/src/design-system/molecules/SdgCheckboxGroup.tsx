import type { SdgIndicator } from '../../types';

const ODS_NAMES: Record<number, string> = {
  1: 'Fin de la pobreza',
  2: 'Hambre cero',
  3: 'Salud y bienestar',
  4: 'Educación de calidad',
  5: 'Igualdad de género',
  6: 'Agua limpia y saneamiento',
  7: 'Energía limpia y asequible',
  8: 'Trabajo decente y crecimiento económico',
  9: 'Industria, innovación e infraestructura',
  10: 'Reducción de las desigualdades',
  11: 'Ciudades y comunidades sostenibles',
  12: 'Producción y consumo responsables',
  13: 'Acción por el clima',
  14: 'Vida submarina',
  15: 'Vida de ecosistemas terrestres',
  16: 'Paz, justicia e instituciones sólidas',
  17: 'Alianzas para lograr los objetivos',
};

function odsNumber(code: string): number {
  const m = code.match(/\d+/);
  return m ? Number(m[0]) : 0;
}

interface Props {
  options: SdgIndicator[];
  selectedIds: number[];
  onChange: (ids: number[]) => void;
}

export function SdgCheckboxGroup({ options, selectedIds, onChange }: Props) {
  function toggle(id: number) {
    onChange(selectedIds.includes(id) ? selectedIds.filter((i) => i !== id) : [...selectedIds, id]);
  }

  return (
    <div className="molecule-sdg-group">
      {options.map((o) => {
        const num = odsNumber(o.code);
        const label = `ODS ${num}: ${ODS_NAMES[num] || o.name}`;
        return (
          <label key={o.id} className="molecule-sdg-option">
            <input type="checkbox" checked={selectedIds.includes(o.id)} onChange={() => toggle(o.id)} />
            <span>{label}</span>
          </label>
        );
      })}
    </div>
  );
}
