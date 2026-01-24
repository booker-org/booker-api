INSERT INTO authors (id, name, biography)
VALUES
  (
    'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d',
    'George Orwell',
    'Eric Arthur Blair, conhecido pelo pseudônimo George Orwell, foi um escritor, jornalista e ensaísta político inglês, nascido na Índia Britânica.'
  ),
  (
    'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e',
    'J.K. Rowling',
    'Joanne Rowling, mais conhecida como J.K. Rowling, é uma escritora britânica, reconhecida por escrever a série de livros Harry Potter.'
  ),
  (
    'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f',
    'Gabriel García Márquez',
    'Gabriel José de la Concordia García Márquez foi um escritor, jornalista e ativista político colombiano.'
  ),
  (
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a',
    'Machado de Assis',
    'Joaquim Maria Machado de Assis foi um escritor brasileiro, considerado por muitos críticos o maior nome da literatura brasileira.'
  ),
  (
    'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b',
    'Jane Austen',
    'Jane Austen foi uma escritora inglesa. A ironia que utilizou para descrever as personagens dos seus romances coloca-a entre os clássicos.'
  ),
  (
    'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0c',
    'Clarice Lispector',
    'Clarice Lispector foi uma escritora e jornalista nascida na Ucrânia e naturalizada brasileira.'
  ),
  (
    'a7b8c9d0-e1f2-4a3b-4c5d-6e7f8a9b0c1d',
    'Stephen King',
    'Stephen Edwin King é um escritor norte-americano de terror, ficção sobrenatural, suspense, ficção científica e fantasia.'
  ),
  (
    'b8c9d0e1-f2a3-4b4c-5d6e-7f8a9b0c1d2e',
    'Agatha Christie',
    'Agatha Mary Clarissa Christie foi uma escritora britânica que atuou como romancista, contista, dramaturga e poetisa.'
  ),
  (
    'c9d0e1f2-a3b4-4c5d-6e7f-8a9b0c1d2e3f',
    'J.R.R. Tolkien',
    'John Ronald Reuel Tolkien foi um escritor, professor universitário e filólogo britânico, nascido na África do Sul.'
  ),
  (
    'd0e1f2a3-b4c5-4d6e-7f8a-9b0c1d2e3f4a',
    'Paulo Coelho',
    'Paulo Coelho de Souza é um romancista, contista, dramaturgo e letrista brasileiro.'
  ),
  (
    'e1f2a3b4-c5d6-4e7f-8a9b-0c1d2e3f4a5b',
    'Virginia Woolf',
    'Adeline Virginia Woolf foi uma escritora, ensaísta e editora britânica, conhecida como uma das mais proeminentes figuras do modernismo.'
  ),
  (
    'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6c',
    'Ernest Hemingway',
    'Ernest Miller Hemingway foi um escritor norte-americano e jornalista. Seu estilo econômico e minimalista teve uma forte influência na ficção do século XX.'
  ),
  (
    'a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7d',
    'Jorge Amado',
    'Jorge Leal Amado de Faria foi um dos mais famosos e traduzidos escritores brasileiros de todos os tempos.'
  ),
  (
    'b4c5d6e7-f8a9-4b0c-1d2e-3f4a5b6c7d8e',
    'Margaret Atwood',
    'Margaret Eleanor Atwood é uma poetisa, romancista, crítica literária e ativista canadense.'
  ),
  (
    'c5d6e7f8-a9b0-4c1d-2e3f-4a5b6c7d8e9f',
    'Haruki Murakami',
    'Haruki Murakami é um escritor japonês e tradutor. Seus livros e contos têm sido best-sellers no Japão e internacionalmente.'
  )
ON CONFLICT (id) DO NOTHING;

INSERT INTO genres (id, name)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'Ficção Científica'),
  ('22222222-2222-2222-2222-222222222222', 'Fantasia'),
  ('33333333-3333-3333-3333-333333333333', 'Romance'),
  ('44444444-4444-4444-4444-444444444444', 'Mistério'),
  ('55555555-5555-5555-5555-555555555555', 'Terror'),
  ('66666666-6666-6666-6666-666666666666', 'Suspense'),
  ('77777777-7777-7777-7777-777777777777', 'Drama'),
  ('88888888-8888-8888-8888-888888888888', 'Aventura'),
  ('99999999-9999-9999-9999-999999999999', 'Distopia'),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Realismo Mágico'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Histórico'),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Clássico'),
  ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Ficção Contemporânea'),
  ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Psicológico'),
  ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Filosófico')
ON CONFLICT (id) DO NOTHING;

INSERT INTO books (
  id,
  title,
  synopsis,
  page_count,
  author_id,
  cover_url
)
VALUES
  (
    'b0000001-0000-0000-0000-000000000001',
    '1984',
    'Uma distopia que apresenta um regime político totalitário e manipulador. Winston Smith vive em uma sociedade onde o Grande Irmão tudo vê e controla.',
    328,
    'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d',
    'https://covers.openlibrary.org/b/id/7222246-L.jpg'
  ),
  (
    'b0000002-0000-0000-0000-000000000002',
    'A Revolução dos Bichos',
    'Uma fábula satírica sobre uma fazenda onde os animais se rebelam contra os humanos e tentam criar uma sociedade igualitária.',
    144,
    'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d',
    'https://covers.openlibrary.org/b/id/8232974-L.jpg'
  ),
  (
    'b0000003-0000-0000-0000-000000000003',
    'Harry Potter e a Pedra Filosofal',
    'Harry Potter descobre que é um bruxo e inicia sua jornada na Escola de Magia e Bruxaria de Hogwarts.',
    309,
    'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e',
    'https://covers.openlibrary.org/b/id/10521270-L.jpg'
  ),
  (
    'b0000004-0000-0000-0000-000000000004',
    'Cem Anos de Solidão',
    'A história da família Buendía ao longo de sete gerações na cidade fictícia de Macondo.',
    417,
    'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f',
    'https://covers.openlibrary.org/b/id/8564515-L.jpg'
  ),
  (
    'b0000005-0000-0000-0000-000000000005',
    'Dom Casmurro',
    'Bentinho narra sua história de amor e ciúmes por Capitu, questionando sua fidelidade.',
    256,
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a',
    'https://covers.openlibrary.org/b/id/8735573-L.jpg'
  ),
  (
    'b0000006-0000-0000-0000-000000000006',
    'Orgulho e Preconceito',
    'Elizabeth Bennet e Mr. Darcy superam suas diferenças e preconceitos para encontrar o amor.',
    432,
    'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b',
    'https://covers.openlibrary.org/b/id/8300065-L.jpg'
  ),
  (
    'b0000007-0000-0000-0000-000000000007',
    'A Hora da Estrela',
    'A história de Macabéa, uma jovem nordestina que vive no Rio de Janeiro em condições precárias.',
    88,
    'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0c',
    'https://covers.openlibrary.org/b/id/8723947-L.jpg'
  ),
  (
    'b0000008-0000-0000-0000-000000000008',
    'O Iluminado',
    'Jack Torrance torna-se zelador de um hotel isolado durante o inverno, onde forças sobrenaturais ameaçam sua sanidade.',
    447,
    'a7b8c9d0-e1f2-4a3b-4c5d-6e7f8a9b0c1d',
    'https://covers.openlibrary.org/b/id/8228691-L.jpg'
  ),
  (
    'b0000009-0000-0000-0000-000000000009',
    'Assassinato no Expresso do Oriente',
    'Hercule Poirot investiga um assassinato ocorrido em um trem bloqueado pela neve.',
    256,
    'b8c9d0e1-f2a3-4b4c-5d6e-7f8a9b0c1d2e',
    'https://covers.openlibrary.org/b/id/8234149-L.jpg'
  ),
  (
    'b0000010-0000-0000-0000-000000000010',
    'O Senhor dos Anéis: A Sociedade do Anel',
    'Frodo Bolseiro inicia sua jornada para destruir o Um Anel e salvar a Terra-média.',
    423,
    'c9d0e1f2-a3b4-4c5d-6e7f-8a9b0c1d2e3f',
    'https://covers.openlibrary.org/b/id/8235833-L.jpg'
  ),
  (
    'b0000011-0000-0000-0000-000000000011',
    'O Alquimista',
    'Santiago, um jovem pastor, viaja em busca de um tesouro nas pirâmides do Egito.',
    208,
    'd0e1f2a3-b4c5-4d6e-7f8a-9b0c1d2e3f4a',
    'https://covers.openlibrary.org/b/id/8235849-L.jpg'
  ),
  (
    'b0000012-0000-0000-0000-000000000012',
    'Mrs. Dalloway',
    'Um dia na vida de Clarissa Dalloway, uma mulher da alta sociedade londrina pós-Primeira Guerra Mundial.',
    194,
    'e1f2a3b4-c5d6-4e7f-8a9b-0c1d2e3f4a5b',
    'https://covers.openlibrary.org/b/id/8235865-L.jpg'
  ),
  (
    'b0000013-0000-0000-0000-000000000013',
    'O Velho e o Mar',
    'Um velho pescador cubano luta contra um enorme marlim no Golfo do México.',
    127,
    'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6c',
    'https://covers.openlibrary.org/b/id/8235878-L.jpg'
  ),
  (
    'b0000014-0000-0000-0000-000000000014',
    'Capitães da Areia',
    'A história de um grupo de meninos de rua em Salvador que vivem de pequenos furtos.',
    280,
    'a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7d',
    'https://covers.openlibrary.org/b/id/8235891-L.jpg'
  ),
  (
    'b0000015-0000-0000-0000-000000000015',
    'O Conto da Aia',
    'Em uma sociedade totalitária futurista, mulheres férteis são forçadas a ter filhos para a elite.',
    311,
    'b4c5d6e7-f8a9-4b0c-1d2e-3f4a5b6c7d8e',
    'https://covers.openlibrary.org/b/id/8235904-L.jpg'
  )
ON CONFLICT (id) DO NOTHING;

INSERT INTO book_genres (book_id, genre_id)
VALUES
  -- 1984: Ficção Científica, Distopia
  ('b0000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111'),
  ('b0000001-0000-0000-0000-000000000001', '99999999-9999-9999-9999-999999999999'),

  -- A Revolução dos Bichos: Ficção Científica, Distopia
  ('b0000002-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111'),
  ('b0000002-0000-0000-0000-000000000002', '99999999-9999-9999-9999-999999999999'),

  -- Harry Potter: Fantasia, Aventura
  ('b0000003-0000-0000-0000-000000000003', '22222222-2222-2222-2222-222222222222'),
  ('b0000003-0000-0000-0000-000000000003', '88888888-8888-8888-8888-888888888888'),

  -- Cem Anos de Solidão: Realismo Mágico, Clássico
  ('b0000004-0000-0000-0000-000000000004', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
  ('b0000004-0000-0000-0000-000000000004', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),

  -- Dom Casmurro: Clássico, Romance, Psicológico
  ('b0000005-0000-0000-0000-000000000005', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
  ('b0000005-0000-0000-0000-000000000005', '33333333-3333-3333-3333-333333333333'),
  ('b0000005-0000-0000-0000-000000000005', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),

  -- Orgulho e Preconceito: Romance, Clássico
  ('b0000006-0000-0000-0000-000000000006', '33333333-3333-3333-3333-333333333333'),
  ('b0000006-0000-0000-0000-000000000006', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),

  -- A Hora da Estrela: Drama, Ficção Contemporânea
  ('b0000007-0000-0000-0000-000000000007', '77777777-7777-7777-7777-777777777777'),
  ('b0000007-0000-0000-0000-000000000007', 'dddddddd-dddd-dddd-dddd-dddddddddddd'),

  -- O Iluminado: Terror, Suspense, Psicológico
  ('b0000008-0000-0000-0000-000000000008', '55555555-5555-5555-5555-555555555555'),
  ('b0000008-0000-0000-0000-000000000008', '66666666-6666-6666-6666-666666666666'),
  ('b0000008-0000-0000-0000-000000000008', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),

  -- Assassinato no Expresso: Mistério, Suspense
  ('b0000009-0000-0000-0000-000000000009', '44444444-4444-4444-4444-444444444444'),
  ('b0000009-0000-0000-0000-000000000009', '66666666-6666-6666-6666-666666666666'),

  -- O Senhor dos Anéis: Fantasia, Aventura
  ('b0000010-0000-0000-0000-000000000010', '22222222-2222-2222-2222-222222222222'),
  ('b0000010-0000-0000-0000-000000000010', '88888888-8888-8888-8888-888888888888'),

  -- O Alquimista: Aventura, Filosófico
  ('b0000011-0000-0000-0000-000000000011', '88888888-8888-8888-8888-888888888888'),
  ('b0000011-0000-0000-0000-000000000011', 'ffffffff-ffff-ffff-ffff-ffffffffffff'),

  -- Mrs. Dalloway: Drama, Psicológico, Clássico
  ('b0000012-0000-0000-0000-000000000012', '77777777-7777-7777-7777-777777777777'),
  ('b0000012-0000-0000-0000-000000000012', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),
  ('b0000012-0000-0000-0000-000000000012', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),

  -- O Velho e o Mar: Aventura, Drama, Clássico
  ('b0000013-0000-0000-0000-000000000013', '88888888-8888-8888-8888-888888888888'),
  ('b0000013-0000-0000-0000-000000000013', '77777777-7777-7777-7777-777777777777'),
  ('b0000013-0000-0000-0000-000000000013', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),

  -- Capitães da Areia: Drama, Histórico
  ('b0000014-0000-0000-0000-000000000014', '77777777-7777-7777-7777-777777777777'),
  ('b0000014-0000-0000-0000-000000000014', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),

  -- O Conto da Aia: Distopia, Ficção Científica
  ('b0000015-0000-0000-0000-000000000015', '99999999-9999-9999-9999-999999999999'),
  ('b0000015-0000-0000-0000-000000000015', '11111111-1111-1111-1111-111111111111')
ON CONFLICT (book_id, genre_id) DO NOTHING;

-- Nota: Senhas são bcrypt hash de "password123" para desenvolvimento
INSERT INTO users (
  id,
  name,
  username,
  email,
  password,
  bio,
  role
) VALUES
  (
    '11000001-0000-0000-0000-000000000001',
    'Admin',
    'admin',
    'admin@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Admin',
    'ADMIN'
  ),

  (
    '10000001-0000-0000-0000-000000000001',
    'Ana Silva',
    'ana.silva',
    'ana.silva@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Apaixonada por literatura clássica e moderna.',
    'USER'
  ),
  (
    '20000002-0000-0000-0000-000000000002',
    'Bruno Costa',
    'bruno.costa',
    'bruno.costa@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Leitor voraz de ficção científica.',
    'USER'
  ),
  (
    '30000003-0000-0000-0000-000000000003',
    'Carla Santos',
    'carla.santos',
    'carla.santos@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Amante de romances e histórias de amor.',
    'USER'
  ),
  (
    '40000004-0000-0000-0000-000000000004',
    'Daniel Oliveira',
    'daniel.oliveira',
    'daniel.oliveira@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Fã de mistério e suspense.',
    'USER'
  ),
  (
    '50000005-0000-0000-0000-000000000005',
    'Eduardo Lima',
    'eduardo.lima',
    'eduardo.lima@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Entusiasta de terror e horror.',
    'USER'
  ),
  (
    '60000006-0000-0000-0000-000000000006',
    'Fernanda Alves',
    'fernanda.alves',
    'fernanda.alves@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Leitora de fantasia e aventura.',
    'USER'
  ),
  (
    '70000007-0000-0000-0000-000000000007',
    'Gabriel Pereira',
    'gabriel.pereira',
    'gabriel.pereira@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Apreciador de dramas intensos.',
    'USER'
  ),
  (
    '80000008-0000-0000-0000-000000000008',
    'Helena Rodrigues',
    'helena.rodrigues',
    'helena.rodrigues@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Interessada em literatura filosófica.',
    'USER'
  ),
  (
    '90000009-0000-0000-0000-000000000009',
    'Igor Fernandes',
    'igor.fernandes',
    'igor.fernandes@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Leitor de histórias de aventura.',
    'USER'
  ),
  (
    'a0000010-0000-0000-0000-000000000010',
    'Julia Martins',
    'julia.martins',
    'julia.martins@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Fã de realismo mágico.',
    'USER'
  ),
  (
    'b0000011-0000-0000-0000-000000000011',
    'Lucas Ribeiro',
    'lucas.ribeiro',
    'lucas.ribeiro@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Admirador de clássicos da literatura.',
    'USER'
  ),
  (
    'c0000012-0000-0000-0000-000000000012',
    'Mariana Souza',
    'mariana.souza',
    'mariana.souza@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Leitora de ficção contemporânea.',
    'USER'
  ),
  (
    'd0000013-0000-0000-0000-000000000013',
    'Nicolas Araujo',
    'nicolas.araujo',
    'nicolas.araujo@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Interessado em psicologia e mente humana.',
    'USER'
  ),
  (
    'e0000014-0000-0000-0000-000000000014',
    'Olivia Carvalho',
    'olivia.carvalho',
    'olivia.carvalho@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Apaixonada por romances históricos.',
    'USER'
  ),
  (
    'f0000015-0000-0000-0000-000000000015',
    'Pedro Gomes',
    'pedro.gomes',
    'pedro.gomes@email.com',
    '$2a$12$.USjv6yuJpWy2dd1E3xrq.9fHdsFiT9rkVAhdGvQZFIbSPqbvYDta',
    'Leitor de distopias e futuros alternativos.',
    'USER'
  )
;