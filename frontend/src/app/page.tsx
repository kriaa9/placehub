"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { motion } from "framer-motion"
import { Heart, Share2, Compass } from "lucide-react"
import Link from "next/link"

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-cream">
      {/* Navigation */}
      <nav className="flex items-center justify-between p-6 max-w-7xl mx-auto">
        <div className="text-2xl font-bold font-display text-charcoal">
          Corner 📍
        </div>
        <div className="space-x-4">
          <Link href="/login">
            <Button variant="ghost">Log in</Button>
          </Link>
          <Link href="/signup">
            <Button variant="default" className="bg-charcoal text-white hover:bg-charcoal/90">
              Sign up
            </Button>
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <main className="max-w-7xl mx-auto px-6 pt-12 pb-24">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
          >
            <h1 className="text-5xl md:text-7xl font-bold font-display leading-tight text-charcoal mb-6">
              Map your world through places that match your <span className="text-transparent bg-clip-text bg-gradient-to-r from-lavender to-sky">vibe</span>.
            </h1>
            <p className="text-xl text-charcoal/80 mb-8 max-w-lg">
              Save cafés, restaurants and hidden gems — and share them with friends. No more 5-star ratings, just vibes.
            </p>
            <div className="flex gap-4">
              <Link href="/signup">
                <Button size="lg" className="text-lg px-8 py-6 rounded-full bg-charcoal text-white hover:bg-charcoal/90">
                  Start Mapping
                </Button>
              </Link>
              <Link href="/demo">
                <Button variant="outline" size="lg" className="text-lg px-8 py-6 rounded-full border-2">
                  Explore Demo
                </Button>
              </Link>
            </div>
          </motion.div>

          {/* Visual Preview */}
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.6, delay: 0.2 }}
            className="relative"
          >
            <div className="absolute top-0 right-0 w-72 h-72 bg-lavender/30 rounded-full blur-3xl -z-10" />
            <div className="absolute bottom-0 left-0 w-72 h-72 bg-peach/30 rounded-full blur-3xl -z-10" />
            
            <div className="grid gap-6">
              <Card className="bg-white/80 backdrop-blur-sm border-none shadow-xl transform rotate-2 hover:rotate-0 transition-transform duration-300">
                <CardContent className="p-6 flex items-center gap-4">
                  <div className="bg-lavender p-3 rounded-full">
                    <Compass className="w-6 h-6 text-charcoal" />
                  </div>
                  <div>
                    <h3 className="font-bold text-lg">Curated Lists</h3>
                    <p className="text-sm text-gray-500">&quot;Best Matcha in NYC&quot; 🍵</p>
                  </div>
                </CardContent>
              </Card>

              <Card className="bg-white/80 backdrop-blur-sm border-none shadow-xl transform -rotate-2 hover:rotate-0 transition-transform duration-300 ml-12">
                <CardContent className="p-6 flex items-center gap-4">
                  <div className="bg-peach p-3 rounded-full">
                    <Heart className="w-6 h-6 text-charcoal" />
                  </div>
                  <div>
                    <h3 className="font-bold text-lg">Vibe Check</h3>
                    <p className="text-sm text-gray-500">Saved &quot;Cozy Study Spot&quot; 📚</p>
                  </div>
                </CardContent>
              </Card>

              <Card className="bg-white/80 backdrop-blur-sm border-none shadow-xl transform rotate-1 hover:rotate-0 transition-transform duration-300">
                <CardContent className="p-6 flex items-center gap-4">
                  <div className="bg-sky p-3 rounded-full">
                    <Share2 className="w-6 h-6 text-charcoal" />
                  </div>
                  <div>
                    <h3 className="font-bold text-lg">Social Sharing</h3>
                    <p className="text-sm text-gray-500">Sent list to @sarah ✨</p>
                  </div>
                </CardContent>
              </Card>
            </div>
          </motion.div>
        </div>
      </main>
    </div>
  )
}
