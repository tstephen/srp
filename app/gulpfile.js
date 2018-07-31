var del         = require('del');
var log         = require('fancy-log');
var gulp        = require('gulp');
var gutil       = require('gulp-util');
var jshint      = require('gulp-jshint');
var uglify      = require('gulp-uglify');
var concat      = require('gulp-concat');
var less        = require('gulp-less');
var minifyCSS   = require('gulp-minify-css');
var prefix      = require('gulp-autoprefixer');
var replace     = require('gulp-replace');
var scp         = require('gulp-scp2');
var zip         = require('gulp-zip');
var vsn         = '2.1.0';

var env = gutil.env.env || 'dev';
log.warn('ENVIRONMENT SET TO: '+env);
var config = require('./config.js')[env];

gulp.task('clean', function(done) {
  return del(['dist'], done);
});

gulp.task('assets', function() {
  // FAQ
  gulp.src([ 'src/faq/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist'));
  gulp.src([ 'src/faq/partials/**/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist/faq/'+vsn+'/partials/'));
  // SRP
  gulp.src([ 'src/srp/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist'));
  gulp.src([ 'src/srp/partials/**/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist/srp/'+vsn+'/partials/'));
  gulp.src([ 'src/**/*.json', 'src/**/*.png' ])
      .pipe(gulp.dest('dist/'));
  gulp.src([ 'src/sdat/**/*' ])
      .pipe(gulp.dest('dist/sdat'));
  return gulp.src([ 'questionnaire/**/*' ])
      .pipe(gulp.dest('dist/questionnaire'));
});

gulp.task('scripts', function() {
  gulp.src([ 'src/sw.js' ])
      .pipe(gulp.dest('dist'));
  return gulp.src([
    'src/srp/js/**/*.js'
  ])
  .pipe(config.js.uglify ? uglify({ mangle: true }) : gutil.noop())
  .pipe(replace('/vsn/', '/'+vsn+'/'))
  .pipe(gulp.dest('dist/srp/'+vsn+'/js'));
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
  return gulp.src([
    'src/srp/css/**/*.css'
  ])
  .pipe(config.css.minify ? minifyCSS() : gutil.noop())
  .pipe(gulp.dest('dist/srp/'+vsn+'/css'));
});

gulp.task('compile',
  gulp.series(/*'test',*/ 'scripts', 'styles')
);

gulp.task('fix-paths', function() {
  gulp.src([
      'src/**/*.html',
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', config.apiServerUrl))
    .pipe(gulp.dest('dist'));
  gulp.src([
      'src/faq/public/*.html'
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', config.apiServerUrl))
    .pipe(gulp.dest('dist/faq/public'));
  gulp.src([
      'src/srp/*.html',
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', config.apiServerUrl))
    .pipe(gulp.dest('dist/srp'));
  return gulp.src([
      'src/srp/public/*.html'
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', config.apiServerUrl))
    .pipe(gulp.dest('dist/srp/public'));
});

gulp.task('package', () =>
  gulp.src(['dist/*','!dist/*.zip'])
      .pipe(zip('archive.zip'))
      .pipe(gulp.dest('dist'))
);

gulp.task('install',
  gulp.series('compile', 'assets', 'fix-paths', 'package')
);

gulp.task('default',
  gulp.series('install')
);

gulp.task('_deploy', function() {
  if (config.server != undefined) {
    return gulp.src(['dist/**/*','!dist/archive.zip'])
    .pipe(scp({
      host: config.server.host,
      username: config.server.usr,
      privateKey: require('fs').readFileSync(config.server.privateKey),
      dest: config.server.dir
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

