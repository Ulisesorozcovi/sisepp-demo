CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    activity_id UUID NOT NULL REFERENCES activities(id) ON DELETE RESTRICT,
    name VARCHAR(200) NOT NULL,
    physical_goal_value NUMERIC(12,2) NOT NULL,
    physical_goal_unit VARCHAR(50) NOT NULL,
    due_date DATE NOT NULL,
    responsible_name VARCHAR(150) NOT NULL,
    responsible_user_id UUID NULL REFERENCES users(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA' CHECK (status IN ('PROGRAMADA','EN_EJECUCION','RETRASADA','COMPLETADA')),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE task_reschedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    previous_due_date DATE NOT NULL,
    new_due_date DATE NOT NULL,
    justification VARCHAR(500) NOT NULL,
    changed_by_user_id UUID NULL REFERENCES users(id) ON DELETE SET NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_tasks_activity ON tasks(activity_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_task_reschedules_task ON task_reschedules(task_id);
