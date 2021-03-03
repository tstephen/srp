var del         = require('del');
var log         = require('fancy-log');
var gulp        = require('gulp');
var babel       = require('gulp-babel');
var jshint      = require('gulp-jshint');
var cleanCSS    = require('gulp-clean-css');
var minimist    = require('minimist');
var replace     = require('gulp-replace');
var rsync       = require('gulp-rsync');
var through2    = require('through2');
var zip         = require('gulp-zip');
var vsn         = '3.0.0-SNAPSHOT';

var buildDir = 'target/classes';
var finalName = 'srp-ui-'+vsn+'.jar'

var argv = minimist(process.argv.slice(2));
var env = argv['env'] || 'dev';
log.warn('ENVIRONMENT SET TO: '+env);
var config = require('./config.js')[env];

gulp.task('clean', function(done) {
  return del([buildDir], done);
});

gulp.task('assets', function() {
  gulp.src([ 'src/**/*.jpg', 'src/**/*.json', 'src/**/*.ico', 'src/**/*.png', 'src/**/*.svg' ])
      .pipe(gulp.dest(buildDir+'/'));
  gulp.src([ 'src/sdat/**/*.png' ])
      .pipe(gulp.dest(buildDir+'/sdat'));
  return gulp.src([ 'questionnaire/**/*' ])
      .pipe(gulp.dest(buildDir+'/questionnaire'));
});

gulp.task('scripts', function() {
  //gulp.src([ 'src/sw.js' ])
  //    .pipe(gulp.dest(buildDir));
  return gulp.src([
    'src/srp/js/**/*.js', '!src/srp/js/**/toast.js', '!src/srp/js/**/*.min.js'
  ])
  .pipe(replace('/vsn/', '/'+vsn+'/'))
  .pipe(config.js.minify ? babel({ presets: [ ["minify", { "builtIns": false }] ] }) : through2.obj())
  .pipe(gulp.dest(buildDir+'/srp/'+vsn+'/js'));
});

gulp.task('test', function() {
  return gulp.src([
    'src/srp/js/**/*.js',
    '!src/srp/js/vendor/**/*.js'
  ])
  .pipe(jshint())
  .pipe(jshint.reporter('default'))
  .pipe(jshint.reporter('fail'));
});

gulp.task('styles', function() {
  gulp.src([
    'src/sdu/css/**/*.css'
  ])
  .pipe(config.css.minify ? cleanCSS() : through2.obj())
  .pipe(gulp.dest(buildDir+'/sdu/css'));
  return gulp.src([
    'src/srp/css/**/*.css'
  ])
  .pipe(config.css.minify ? cleanCSS() : through2.obj())
  .pipe(gulp.dest(buildDir+'/srp/'+vsn+'/css'));
});

gulp.task('compile',
  gulp.series(/*'test',*/ 'scripts', 'styles')
);

gulp.task('server', function(done) {
  bSync({
    server: {
      baseDir: [buildDir]
    }
  });
  gulp.watch(
    [ 'src/manifest.json', 'src/**/*.html' ],
    gulp.parallel('assets')
  );
  gulp.watch(
    ['src/**/*.js'],
    gulp.parallel('scripts')
  );
  gulp.watch(
    'src/css/**/*.css',
    gulp.parallel('styles')
  );
  gulp.watch(
    buildDir+'/**/*',
    bSync.reload
  );
  done();
});

gulp.task('fix-paths', function() {
  // SHARED
  gulp.src([ 'src/partials/**/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest(buildDir+'/partials/'));
  // FAQ
  gulp.src([
      'src/faq/public/*.html'
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', config.apiServerUrl))
    .pipe(gulp.dest(buildDir+'/faq/public'));
  gulp.src([ 'src/faq/partials/**/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest(buildDir+'/faq/'+vsn+'/partials/'));
  // SRP
  gulp.src([
      'src/srp/*.html',
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', config.apiServerUrl))
    .pipe(gulp.dest(buildDir+'/srp'));
  gulp.src([ 'src/srp/partials/**/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest(buildDir+'/srp/'+vsn+'/partials/'));
  return gulp.src([
      'src/srp/public/*.html'
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', config.apiServerUrl))
    .pipe(gulp.dest(buildDir+'/srp/public'));
});

gulp.task('package', () =>
  gulp.src([buildDir+'/*','!'+buildDir+'/*.zip'])
      .pipe(zip(finalName))
      .pipe(gulp.dest(buildDir))
);

gulp.task('install',
  gulp.series('compile', 'assets', 'fix-paths', 'package')
);

gulp.task('default',
  gulp.series('install')
);

gulp.task('_deploy', function() {
  log.warn('Deploying to '+env);
  if (config.server != undefined) {
    return gulp.src(buildDir+'/**')
    .pipe(rsync({
      root: buildDir+'/',
      hostname: config.server.host,
      destination: config.server.dir,
      archive: false,
      silent: false,
      compress: true
    }))
    .on('error', function(err) {
      console.log(err);
    });
  } else {
    log.error('No config.server specified for '+env);
  }
});

gulp.task('deploy',
  gulp.series('install', '_deploy')
);

