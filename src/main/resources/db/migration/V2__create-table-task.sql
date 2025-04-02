CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- uuid aleatório gerado como valor padrão
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    due_date TEXT NOT NULL,
    task_status VARCHAR(20) NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);