export interface Vehicle {
    id: string;
    placa: string;
    marca: string;
    modelo: string;
    anio: number;
    category: string;
    descripcion: string;
    isActive: boolean;
}

export interface VehicleFormData {
    placa: string;
    marca: string;
    modelo: string;
    anio: number;
    category: string;
    descripcion: string;
    isActive: boolean;
}
