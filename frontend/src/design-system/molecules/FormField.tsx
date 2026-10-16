import type { InputHTMLAttributes } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';

interface Props extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

export function FormField({ label, error, id, ...rest }: Props) {
  return (
    <div className="molecule-field">
      <Label htmlFor={id}>{label}</Label>
      <Input id={id} {...rest} />
      {error && <div className="molecule-field-error">{error}</div>}
    </div>
  );
}
