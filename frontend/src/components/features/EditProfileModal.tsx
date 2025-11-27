"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { X } from "lucide-react"
import { motion, AnimatePresence } from "framer-motion"
import api from "@/lib/api"

interface EditProfileModalProps {
  isOpen: boolean
  onClose: () => void
  onUpdateSuccess: (updatedProfile: any) => void
  onDeleteAccount: () => void
  initialData: {
    firstName: string
    lastName: string
    bio?: string
  }
}

export function EditProfileModal({
  isOpen,
  onClose,
  onUpdateSuccess,
  onDeleteAccount,
  initialData,
}: EditProfileModalProps) {
  const [formData, setFormData] = useState(initialData)
  const [loading, setLoading] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      const response = await api.put("/profile/me", formData)
      onUpdateSuccess(response.data)
      onClose()
    } catch (error) {
      console.error("Failed to update profile:", error)
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 0.95 }}
          className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6"
        >
          {!showDeleteConfirm ? (
            <>
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-charcoal">Edit Profile</h2>
                <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
                  <X className="h-6 w-6" />
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <label className="text-sm font-medium text-gray-700">First Name</label>
                    <Input
                      name="firstName"
                      value={formData.firstName}
                      onChange={handleChange}
                      required
                      className="bg-white"
                    />
                  </div>
                  <div className="space-y-2">
                    <label className="text-sm font-medium text-gray-700">Last Name</label>
                    <Input
                      name="lastName"
                      value={formData.lastName}
                      onChange={handleChange}
                      required
                      className="bg-white"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">Bio</label>
                  <textarea
                    name="bio"
                    value={formData.bio || ""}
                    onChange={handleChange}
                    className="flex w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm ring-offset-background placeholder:text-gray-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 min-h-[100px]"
                    placeholder="Tell us about yourself..."
                  />
                </div>

                <div className="pt-4 flex gap-3">
                  <Button type="button" variant="outline" onClick={onClose} className="flex-1">
                    Cancel
                  </Button>
                  <Button type="submit" disabled={loading} className="flex-1 bg-charcoal text-white hover:bg-charcoal/90">
                    {loading ? "Saving..." : "Save Changes"}
                  </Button>
                </div>
              </form>

              <div className="mt-8 pt-6 border-t border-gray-100">
                <button
                  onClick={() => setShowDeleteConfirm(true)}
                  className="text-red-500 text-sm font-medium hover:text-red-600 transition-colors w-full text-center"
                >
                  Delete Account
                </button>
              </div>
            </>
          ) : (
            <div className="text-center space-y-4">
              <h3 className="text-xl font-bold text-red-600">Delete Account?</h3>
              <p className="text-gray-600">
                This action cannot be undone. All your lists and saved places will be permanently deleted.
              </p>
              <div className="flex gap-3 pt-4">
                <Button
                  variant="outline"
                  onClick={() => setShowDeleteConfirm(false)}
                  className="flex-1"
                >
                  Cancel
                </Button>
                <Button
                  onClick={onDeleteAccount}
                  className="flex-1 bg-red-600 text-white hover:bg-red-700"
                >
                  Yes, Delete
                </Button>
              </div>
            </div>
          )}
        </motion.div>
      </div>
    </AnimatePresence>
  )
}
