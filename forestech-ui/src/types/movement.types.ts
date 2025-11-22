export interface Movement {
    id: string;
    movementType: 'ENTRADA' | 'SALIDA';
    productId: string;
    vehicleId?: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
    description: string;
    createdAt: string;
}

export interface MovementFormData {
    movementType: 'ENTRADA' | 'SALIDA';
    productId: string;
    vehicleId?: string;
    quantity: number;
    unitPrice: number;
    description: string;
}
