# System compilation

As the system consists of several modules, the compilation of the entire project can be divided into 3 steps:

1. Compilation of the Backend (i.e. agent system)
2. Compilation of the GUI
3. Compilation of the Socket Server

All these modules can be either **compiled separately** (i.e. using separate compilation scripts - it is useful
especially when the application is run on multiple hosts and some of them run only selected modules) or **compiled all
at once**.

**IMPORTANT! All compilation scripts have to be executed from the ./compile directory!**

## Compiling modules separately

### Compiling agent system

In order to compile the agent system, execute:

```bash
source ./compile-modules/initialize-backend.sh
```

The command will compile modules: _agent-factory_, _application_, _commons_, _engine_, _gui_, _knowledge-database_,
_managing-system_ and _rules-controller_. Specifically, it will:

- install the customized JADE library
- run the Docker database instance (if the container has not been created yet)
- start the database (if it has not been started)
- build all target sources
- place the configuration files in their designated directories

#### Recompiling

To recompile the agent system (in case of any changes) execute:

```bash
source ./compile-modules/recompile-backend.sh
```

### Compiling GUI

In order to compile GUI, execute:

```bash
source ./compile-modules/initialize-GUI.sh
```

The command will compile module _green-cloud-ui_. Specifically, it will:

- place the configuration files in the designated directory
- rebuild node modules

#### Recompiling

To recompile the GUI (in case of any changes) execute:

```bash
source ./compile-modules/recompile-GUI.sh
```

### Compiling Socket Server

In order to compile Socket Server, execute:

```bash
source ./compile-modules/initialize-socket.sh
```

The command will compile module _socket-server_. Specifically, it will:

- place the configuration files in the designated directory
- rebuild node modules
- compile JS libraries using babel

#### Recompiling

To recompile the Socket Server (in case of any changes) execute:

```bash
source ./compile-modules/recompile-socket.sh
```

## Compiling project at once

To compile all project modules, execute the following command:

```bash
source ./compile-project/initialize-project.sh
```

The script will essentially complete all steps mentioned in the previous sections.

#### Recompiling

To recompile the project (in case of any changes) execute:

```bash
source ./compile-project/recompile-project.sh
```

## Running the project

After finishing the compilation, the project modules can be run using following commands.

### Running database

If the database container is not running, use the following command:

```bash
source ./run-modules/run-database.sh
```

### Running agent system

To run project with single host use:

```bash
source ./run-modules/run-backend.sh SINGLE
```

To run project with multiple hosts use:

```bash
source ./run-modules/run-backend.sh MULTI
```

### Running GUI

To run GUI use (GUI will be started on http://localhost:3000/):

```bash
source ./run-modules/run-GUI.sh
```

**Important! Be aware that running a GUI may take a while.**

### Running Socket Server

To run Socket Server use:

```bash
source ./run-modules/run-socket.sh
```

**Important! The Socket Server is started almost instantly (the last console message will be _node index.js_. To verify
the connection between Socket Server and GUI, run GUI and try to connect with the Server using Menu)**.