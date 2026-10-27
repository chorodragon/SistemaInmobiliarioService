ALTER SESSION SET CURRENT_SCHEMA = inmobiliario;

INSERT INTO TipoInmueble (nombre) VALUES ('Casa');
INSERT INTO TipoInmueble (nombre) VALUES ('Departamento');
INSERT INTO TipoInmueble (nombre) VALUES ('Terreno');

INSERT INTO TipoPago (nombre, descripcion) VALUES ('Efectivo', 'Pago al contado');
INSERT INTO TipoPago (nombre, descripcion) VALUES ('Transferencia', 'Transferencia bancaria');
INSERT INTO TipoPago (nombre, descripcion) VALUES ('Credito', 'Credito hipotecario');

INSERT INTO Caracteristicas (caracteristica) VALUES ('Antiguedad');
INSERT INTO Caracteristicas (caracteristica) VALUES ('Modelo');
INSERT INTO Caracteristicas (caracteristica) VALUES ('Material');
INSERT INTO Caracteristicas (caracteristica) VALUES ('Habitaciones');
INSERT INTO Caracteristicas (caracteristica) VALUES ('Banos');

COMMIT;
