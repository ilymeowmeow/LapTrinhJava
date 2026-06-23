"use client";

import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import { CONFIG } from "@/lib/config";

export default function TokenSyncProvider({ children }: { children: React.ReactNode }) {
  const { data: session, status } = useSession();
  const [isSynced, setIsSynced] = useState(false);

  useEffect(() => {
    if (status === "authenticated" && session?.user) {
      // Don't re-sync if we already have a valid token (optional improvement)
      const currentToken = localStorage.getItem("backend_token");
      
      const syncToken = async () => {
        try {
          const res = await fetch(`${CONFIG.API_BASE_URL}/auth/sync`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              email: session.user?.email,
              name: session.user?.name,
              image: session.user?.image,
            }),
          });
          
          if (res.ok) {
            const data = await res.json();
            if (data.token) {
              localStorage.setItem("backend_token", data.token);
            }
          }
        } catch (error) {
          console.error("Failed to sync token with backend:", error);
        } finally {
          setIsSynced(true);
        }
      };

      syncToken();
    } else if (status === "unauthenticated") {
      localStorage.removeItem("backend_token");
      setIsSynced(true);
    }
  }, [session, status]);

  // Optionally, you can wait for sync before rendering children, 
  // but to avoid blocking UI we can just render them.
  return <>{children}</>;
}
