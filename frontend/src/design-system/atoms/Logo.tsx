interface Props {
  size?: number;
  withLabel?: boolean;
}

export function Logo({ size = 34, withLabel = true }: Props) {
  return (
    <div className="atom-logo">
      <svg viewBox="0 0 40 40" fill="none" width={size} height={size}>
        <rect x="4" y="4" width="32" height="32" rx="8" fill="#111111" />
        <rect x="11" y="21" width="5" height="9" rx="1.5" fill="#FFFFFF" />
        <rect x="17.5" y="15" width="5" height="15" rx="1.5" fill="#FFFFFF" fillOpacity="0.8" />
        <rect x="24" y="9" width="5" height="21" rx="1.5" fill="#FFFFFF" fillOpacity="0.6" />
      </svg>
      {withLabel && <span>SISEPP</span>}
    </div>
  );
}
