interface ProfileStatsProps {
  listsCount: number
  savedPlacesCount: number
}

export function ProfileStats({ listsCount, savedPlacesCount }: ProfileStatsProps) {
  return (
    <div className="flex gap-4 justify-center">
      <div className="bg-gradient-to-br from-purple-100 to-pink-100 rounded-full px-6 py-3 shadow-sm">
        <div className="text-2xl font-bold text-charcoal">{listsCount}</div>
        <div className="text-sm text-gray-600">Lists</div>
      </div>
      <div className="bg-gradient-to-br from-blue-100 to-cyan-100 rounded-full px-6 py-3 shadow-sm">
        <div className="text-2xl font-bold text-charcoal">{savedPlacesCount}</div>
        <div className="text-sm text-gray-600">Saved Places</div>
      </div>
    </div>
  )
}
