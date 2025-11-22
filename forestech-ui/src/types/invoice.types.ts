export interface InvoiceDetail {
    idDetalle?: number;
    productId: string | null;  // null cuando es producto nuevo
    productName?: string;      // Para crear productos nuevos (enviar al backend)
    producto: string;
    cantidad: number;
    precioUnitario: number;
    ivaPercent?: number;       // IVA % por línea (default 13%)
}

export interface Invoice {
    id: string;
    numeroFactura: string;
    supplierId: string;
    fechaEmision: string;
    fechaVencimiento: string;
    clienteNombre: string;
    clienteNit: string;
    subtotal: number;
    iva: number;
    total: number;
    observaciones: string;
    formaPago: string;
    cuentaBancaria: string;
    estado: string;
    detalles: InvoiceDetail[];
}

export interface InvoiceFormData {
    numeroFactura: string;
    supplierId: string;
    fechaEmision: string;
    fechaVencimiento: string;
    clienteNombre: string;
    clienteNit: string;
    observaciones: string;
    formaPago: string;
    cuentaBancaria: string;
    detalles: InvoiceDetail[];
}
