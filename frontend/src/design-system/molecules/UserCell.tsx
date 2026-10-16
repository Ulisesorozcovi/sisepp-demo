import { Avatar } from '../atoms/Avatar';

interface Props {
  fullName: string;
  email: string;
}

export function UserCell({ fullName, email }: Props) {
  return (
    <div className="user-cell">
      <Avatar name={fullName} />
      <div>
        <div className="name">{fullName}</div>
        <div className="email">{email}</div>
      </div>
    </div>
  );
}
