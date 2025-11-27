"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ProfileHeader } from "@/components/features/ProfileHeader"
import { ProfileStats } from "@/components/features/ProfileStats"
import { ImageUploadModal } from "@/components/features/ImageUploadModal"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/Tabs"
import { EmptyState } from "@/components/ui/EmptyState"
import api from "@/lib/api"
import { motion } from "framer-motion"

interface UserProfile {
  id: number
  email: string
  firstName: string
  lastName: string
  bio?: string
  avatarUrl?: string
  followersCount: number
  followingCount: number
  isFollowing: boolean
}

export default function ProfilePage() {
  const router = useRouter()
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [uploadModalOpen, setUploadModalOpen] = useState(false)

  useEffect(() => {
    const token = localStorage.getItem("token")
    if (!token) {
      router.push("/login")
      return
    }

    const fetchProfile = async () => {
      try {
        const response = await api.get("/profile/me")
        setProfile(response.data)
      } catch (error) {
        console.error("Failed to fetch profile:", error)
        router.push("/login")
      } finally {
        setLoading(false)
      }
    }

    fetchProfile()
  }, [router])

  const handleUploadSuccess = async (avatarUrl: string) => {
    try {
      const response = await api.put("/profile/me", { avatarUrl })
      setProfile(response.data)
    } catch (error) {
      console.error("Failed to update avatar:", error)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-cream flex items-center justify-center">
        <div className="text-2xl text-charcoal">Loading...</div>
      </div>
    )
  }

  if (!profile) {
    return null
  }

  const username = profile.email.split("@")[0]
  const listsCount = 0 // TODO: Get from backend
  const savedPlacesCount = 0 // TODO: Get from backend

  return (
    <div className="min-h-screen bg-cream">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-4xl mx-auto px-4 py-12"
      >
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-xl p-8 space-y-8">
          <ProfileHeader
            avatarUrl={profile.avatarUrl}
            firstName={profile.firstName}
            lastName={profile.lastName}
            username={username}
            bio={profile.bio}
            onUploadClick={() => setUploadModalOpen(true)}
          />

          <ProfileStats
            listsCount={listsCount}
            savedPlacesCount={savedPlacesCount}
          />

          <Tabs defaultValue="lists" className="w-full">
            <div className="flex justify-center">
              <TabsList>
                <TabsTrigger value="lists">My Lists</TabsTrigger>
                <TabsTrigger value="places">Saved Places</TabsTrigger>
              </TabsList>
            </div>

            <TabsContent value="lists">
              <EmptyState
                icon="📝"
                title="You haven't created any lists yet"
                description="Start building your world by creating your first list."
                actionLabel="Create List"
                onAction={() => {
                  // TODO: Navigate to create list page
                  console.log("Create list")
                }}
              />
            </TabsContent>

            <TabsContent value="places">
              <EmptyState
                icon="🌍"
                title="You haven't saved any places yet"
                description="Save the spots that speak to you."
                actionLabel="Discover Places"
                onAction={() => {
                  // TODO: Navigate to discover page
                  console.log("Discover places")
                }}
              />
            </TabsContent>
          </Tabs>
        </div>
      </motion.div>

      <ImageUploadModal
        isOpen={uploadModalOpen}
        onClose={() => setUploadModalOpen(false)}
        onUploadSuccess={handleUploadSuccess}
      />
    </div>
  )
}
