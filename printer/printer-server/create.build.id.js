const fs          = require('fs');
const AutoVersion = require('auto-version-js');

try {
    const FETCH_HEAD        = fs.readFileSync('../../.git/FETCH_HEAD', {encoding:'utf8', flag:'r'}).split('\n');
    let   FETCH_HEAD_branch = '';

    for(const line of FETCH_HEAD){
        if(line.includes("branch '")){
            FETCH_HEAD_branch = line;
            break;
        }
    }

    let ENV = FETCH_HEAD_branch.split("branch '")[1];
        ENV = ENV.split("'")[0]; 

    let HASH = FETCH_HEAD_branch.split("branch '")[0];
        HASH = HASH.substring(0,8);

    const VERSION = AutoVersion.getVersion();

    const buildId = `v${VERSION}-${HASH}-${ENV}`;

    fs.writeFileSync('./build/build-id.json', JSON.stringify( { buildId : buildId } , null, 2));

    console.log('Successfully created build ID -> ', buildId);
} catch (error) {
    console.error(
        'I was not able to create a build ID\n',
        error
    );
}