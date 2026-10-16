import type { ButtonHTMLAttributes } from 'react';

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'ghost' | 'outline';
}

const VARIANT_CLASS: Record<string, string> = {
  primary: 'atom-btn--primary',
  ghost: 'atom-btn--ghost',
  outline: 'atom-btn--outline',
};

export function Button({ variant = 'primary', className = '', ...rest }: Props) {
  return <button className={`atom-btn ${VARIANT_CLASS[variant]} ${className}`} {...rest} />;
}
