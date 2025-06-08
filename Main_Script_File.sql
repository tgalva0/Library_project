create database Library;
create table Livro(
	id_livro int auto_increment primary key not null,
    titulo varchar(30) not null,
    isbn varchar(30) not null
    );
    
alter table livro add numero_copias int default (1) not null;
    
create table Autor(
	id_autor int auto_increment primary key not null,
    Nome varchar(100)
);

create table livro_autor(
	id_livro int,
    id_autor int,
    primary key(id_livro, id_autor),
    foreign key (id_livro) references Livro(id_livro),
    foreign key (id_autor) references Autor(id_autor)
);

create table Copia_Livro(
	id_copia_livro int auto_increment primary key not null,
    id_livro int not null,
    foreign key (id_livro) references Livro(id_livro),
    status_livro enum('emprestado', 'disponivel') not null
);

CREATE TRIGGER atualiza_quantidade_copias
AFTER INSERT ON Copia_livro
FOR EACH ROW
UPDATE Livro
SET numero_copias = (
    SELECT COUNT(*) FROM Copia_livro WHERE id_livro = NEW.id_livro
)
WHERE id = NEW.id_livro;

CREATE TRIGGER reduz_quantidade_copias
AFTER DELETE ON Copia_livro
FOR EACH ROW
UPDATE Livro
SET numero_copias = (
    SELECT COUNT(*) FROM Copia_livro WHERE id_livro = OLD.id_livro
)
WHERE id = OLD.id_livro;

create table Membros(
	id_membro int auto_increment primary key not null,
    nome varchar(100) not null,
    email varchar(100) not null,
    telefone varchar(14) not null
);

alter table membros add papel enum('bibliotecario', 'cliente') unique default ('cliente') not null;
alter table membros add senha_hash varchar(30) not null;

create table Emprestimo(
	id_emprestimo int auto_increment primary key not null,
    id_copia_livro int not null,
    id_membro int not null,
    data_emprestimo datetime default now(),
    data_devolucao datetime default (date_add(curdate(), interval 7 day)),
    status_emprestimo enum('ativo', 'atrasado', 'concluido') default ('ativo') not null
);

alter table emprestimo modify multa float default (0);
alter table copia_livro modify status_livro enum('emprestado', 'disponivel') default ('disponivel') not null;
