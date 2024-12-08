export interface ImportHistory {
    id: number;
    timestamp: string;
    status: "SUCCESS" | "PENDING" | "FAILURE";
    performedBy: string;
    recordsImported: number;
    errorMessage: string | null;
    fileUrl: string | undefined;
  }