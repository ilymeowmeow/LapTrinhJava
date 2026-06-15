import NextAuth from "next-auth"
import GoogleProvider from "next-auth/providers/google"

const handler = NextAuth({
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID || "PLACEHOLDER_CLIENT_ID",
      clientSecret: process.env.GOOGLE_CLIENT_SECRET || "PLACEHOLDER_CLIENT_SECRET",
    }),
  ],
  secret: process.env.NEXTAUTH_SECRET || "PLACEHOLDER_NEXTAUTH_SECRET_KEY_FOR_DEV_ONLY",
})

export { handler as GET, handler as POST }
