const config = require('config');
async function connect() {
    if (global.connection)
        return global.connection.connect();

    const { Pool } = require('pg');
    const pool = new Pool({
        connectionString: `postgres://${config.get('db.username')}:${config.get('db.password')}@${config.get('db.host')}:${config.get('db.port')}/${config.get('db.name')}`
    });

    //apenas testando a conexão
    const client = await pool.connect();
    console.log("Criou pool de conexões no PostgreSQL!");

    const res = await client.query('SELECT NOW()');
    console.log(res.rows[0]);
    client.release();

    //guardando para usar sempre o mesmo
    global.connection = pool;
    return pool.connect();
}
async function getFaces() {
    const client = await connect();
    const res = await client.query('select id as identification, name from face');
    return await res.rows;
}

async function deleteFace(id){
    const client = await connect();
    const sql = 'DELETE FROM face where id=$1;';
    return await client.query(sql, [id]);
}

module.exports = { getFaces, deleteFace }