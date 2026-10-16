interface Props {
  name: string;
}

function initials(name: string) {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase())
    .join('');
}

export function Avatar({ name }: Props) {
  return <div className="atom-avatar">{initials(name)}</div>;
}
