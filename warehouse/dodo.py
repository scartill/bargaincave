
PY_DEPS = {
    'ecwidorder': [
        'rest',
        'ecwid',
        'dostavista',
        'secrets',
        'bargain_cave_bot'
    ],
    'telegrampublish': [
        'rest',
        'gqlclient',
        'secrets',
        'bargain_cave_bot'
    ],
    'dostaorder': [
        'rest',
        'ecwid',
        'dostavista',
        'secrets',
        'bargain_cave_bot'
    ],
    'warehouseorders': [
        'rest',
        'ecwid',
        'secrets'
    ]
}

def task_prepush():
    def template_dep(source, target):
        source_path = f'common/{source}.py'
        target_path = f'amplify/backend/function/{target}/src/{source}.py'
        return {
            'name': f'update.{source}.{target}',
            'file_dep': [source_path],
            'targets': [target_path],
            'actions': [f'cp {source_path} {target_path}']            
        }

    return (
        template_dep(source, target) 
        for target in PY_DEPS 
        for source in PY_DEPS[target]
    )
