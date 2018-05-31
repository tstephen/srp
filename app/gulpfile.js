var del         = require('del');
var gulp        = require('gulp');
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

gulp.task('clean', function(done) {
  return del(['dist'], done);
});

gulp.task('assets', function() {
  gulp.src([ 'src/faq/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist/faq'));
  gulp.src([ 'src/faq/config/*.json' ])
      .pipe(gulp.dest('dist/faq/config/'));
  gulp.src([ 'src/faq/public/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist/faq/public'));
  gulp.src([ 'src/sdat/**/*' ])
      .pipe(gulp.dest('dist/sdat'));
  gulp.src([ 'src/srp/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist/srp'));
  gulp.src([ 'src/srp/public/*.html' ])
      .pipe(replace('/vsn/', '/'+vsn+'/'))
      .pipe(gulp.dest('dist/srp/public'));
  gulp.src([ 'src/srp/partials/*.html' ])
      .pipe(gulp.dest('dist/srp/'+vsn+'/partials'));
  gulp.src([ 'src/**/*.png' ])
      .pipe(gulp.dest('dist'));
  return gulp.src([ 'questionnaire/**/*' ])
      .pipe(gulp.dest('dist/questionnaire'));
});

gulp.task('scripts', function() {
  gulp.src([ 'src/sw.js' ])
      .pipe(gulp.dest('dist'));
  return gulp.src([
    'src/srp/js/**/*.js'
  ])
  /*.pipe(concat('main.min.js'))
  .pipe(uglify())*/
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
  .pipe(gulp.dest('dist/srp/'+vsn+'/css'));
  /*return gulp.src('srp/styles/main.less')
    .pipe(less())
    .pipe(minifyCSS())
    .pipe(prefix())
    .pipe(gulp.dest('dist/srp/css'));*/
});

gulp.task('compile',
  gulp.series(/*'test',*/ 'scripts', 'styles')
);

gulp.task('dev2prod', function() {
  gulp.src([
      'src/faq/*.html',
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', 'https://api.knowprocess.com'))
    .pipe(gulp.dest('dist/faq'));
  gulp.src([
      'src/faq/public/*.html'
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', 'https://api.knowprocess.com'))
    .pipe(gulp.dest('dist/faq/public'));
  gulp.src([
      'src/srp/*.html',
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', 'https://api.knowprocess.com'))
    .pipe(gulp.dest('dist/srp'));
  return gulp.src([
      'src/srp/public/*.html'
    ])
    .pipe(replace('/vsn/', '/'+vsn+'/'))
    .pipe(replace('http://localhost:8083', 'https://api.knowprocess.com'))
    .pipe(gulp.dest('dist/srp/public'));
});

gulp.task('package', () =>
  gulp.src(['dist/*','!dist/*.zip'])
      .pipe(zip('archive.zip'))
      .pipe(gulp.dest('dist'))
);

gulp.task('install',
  gulp.series('compile', 'assets', 'dev2prod', 'package')
);

gulp.task('default',
  gulp.series('install')
);

gulp.task('deploy', function() {
  return gulp.src(['dist/**/*','!dist/archive.zip'])
  .pipe(scp({
    host: 'srp.digital',
    username: 'tstephen',
    privateKey: require('fs').readFileSync('/home/tstephen/.ssh/id_rsa'),
    dest: '/var/www-srp/'
  }))
  .on('error', function(err) {
    console.log(err);
  });
});
