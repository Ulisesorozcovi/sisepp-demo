import { useState } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';

interface Props {
  id: string;
  label: string;
  value: string;
  onChange: (value: string) => void;
}

export function PasswordField({ id, label, value, onChange }: Props) {
  const [visible, setVisible] = useState(false);

  return (
    <div className="molecule-field">
      <Label htmlFor={id}>{label}</Label>
      <div className="molecule-password">
        <Input
          id={id}
          type={visible ? 'text' : 'password'}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder="••••••••"
          required
        />
        <button type="button" className="toggle" onClick={() => setVisible((v) => !v)}>
          {visible ? 'Ocultar' : 'Mostrar'}
        </button>
      </div>
    </div>
  );
}
