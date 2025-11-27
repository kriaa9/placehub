import { Avatar } from "@/components/ui/Avatar"
import { Button } from "@/components/ui/button"
import { Camera, LogOut, Edit2 } from "lucide-react"

interface ProfileHeaderProps {
  avatarUrl?: string
  firstName: string
  lastName: string
  username: string
  bio?: string
  onUploadClick: () => void
  onEditClick?: () => void
  onLogoutClick?: () => void
}

export function ProfileHeader({
  avatarUrl,
  firstName,
  lastName,
  username,
  bio,
  onUploadClick,
  onEditClick,
  onLogoutClick,
}: ProfileHeaderProps) {
  const initials = `${firstName[0] || ""}${lastName[0] || ""}`.toUpperCase()

  return (
    <div className="flex flex-col items-center text-center space-y-4 relative">
      {onLogoutClick && (
        <button
          onClick={onLogoutClick}
          className="absolute top-0 right-0 p-2 text-gray-400 hover:text-red-500 transition-colors"
          title="Log Out"
        >
          <LogOut className="h-5 w-5" />
        </button>
      )}

      <div className="relative group">
        <Avatar
          src={avatarUrl}
          alt={`${firstName} ${lastName}`}
          fallback={initials}
          className="h-32 w-32 ring-4 ring-white shadow-xl"
        />
        <button
          onClick={onUploadClick}
          className="absolute bottom-0 right-0 bg-charcoal text-white p-3 rounded-full shadow-lg opacity-0 group-hover:opacity-100 transition-opacity"
        >
          <Camera className="h-4 w-4" />
        </button>
      </div>

      <div>
        <h1 className="text-3xl font-bold text-charcoal">
          {firstName} {lastName}
        </h1>
        <p className="text-gray-500">@{username}</p>
      </div>

      {bio ? (
        <p className="text-gray-600 max-w-md">{bio}</p>
      ) : (
        <p className="text-gray-400 italic">Make it yours ✨ Add a bio.</p>
      )}

      {onEditClick && (
        <Button onClick={onEditClick} variant="outline" className="gap-2">
          <Edit2 className="h-4 w-4" />
          Edit Profile
        </Button>
      )}
    </div>
  )
}
