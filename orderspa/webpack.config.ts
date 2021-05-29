/**
 * Define paths to any stylesheets you wish to include at the top of the CSS bundle. Any styles compiled from svelte
 * will be added to the bundle after these. In other words, these are global styles for your svelte app. You can also
 * specify paths to SCSS or SASS files, and they will be compiled automatically.
 */
const stylesheets = [
	'./src/styles/index.scss'
];

/**
 * Change this to `true` to generate source maps alongside your production bundle. This is useful for debugging, but
 * will increase total bundle size and expose your source code.
 */
const sourceMapsInProduction = false;

/*********************************************************************************************************************/
/**********                                             Webpack                                             **********/
/*********************************************************************************************************************/

import Webpack from 'webpack';
import WebpackDev from 'webpack-dev-server';
import SveltePreprocess from 'svelte-preprocess';
import Autoprefixer from 'autoprefixer';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';
import OptimizeCSSAssetsPlugin from 'optimize-css-assets-webpack-plugin';
import Dotenv from 'dotenv-webpack'

import { CleanWebpackPlugin } from 'clean-webpack-plugin';

import fs from 'fs';
import path from 'path';

const mode = process.env.NODE_ENV ?? 'development';
const isProduction = mode === 'production';
const isDevelopment = !isProduction;

const config: Configuration = {
	mode: isProduction ? 'production' : 'development',
	entry: {
		bundle: [
			...stylesheets,
			'./src/main.ts'
		]
	},
	resolve: {
		alias: {
			// Note: Later in this config file, we'll automatically add paths from `tsconfig.compilerOptions.paths`
			svelte: path.resolve('node_modules', 'svelte')
		},
		extensions: ['.mjs', '.js', '.json', '.jsx', '.css', '.ts', '.svelte', '.webpack.js', '.web.js'],
		mainFields: ['svelte', 'browser', 'module', 'main']
	},
	output: {
		path: path.resolve(__dirname, 'dist/build'),
		publicPath: '/build/',
		filename: '[name].js',
		chunkFilename: '[name].[id].js'
	},
	module: {
		rules: [
			// Rule: Svelte
			{
				test: /\.svelte$/,
				use: {
					loader: 'svelte-loader',
					options: {
						compilerOptions: {
							// Dev mode must be enabled for HMR to work!
							dev: isDevelopment
						},
						emitCss: isProduction,
						hotReload: isDevelopment,
						hotOptions: {
							// List of options and defaults: https://www.npmjs.com/package/svelte-loader-hot#usage
							noPreserveState: false,
							optimistic: true,
						},
						preprocess: SveltePreprocess({
							scss: true,
							sass: true,
							postcss: {
								plugins: [
									Autoprefixer
								]
							}
						})
					}
				}
			},

			// Required to prevent errors from Svelte on Webpack 5+, omit on Webpack 4
			// See: https://github.com/sveltejs/svelte-loader#usage
			{
				test: /node_modules\/svelte\/.*\.mjs$/,
				resolve: {
					fullySpecified: false
				}
			},

			{
                test: /\.m?js/,
                resolve: {
                    fullySpecified: false
                }
            },

			// Rule: SASS
			{
				test: /\.(scss|sass)$/,
				use: [
					{
						loader: MiniCssExtractPlugin.loader
					},
					'css-loader',
					{
						loader: 'postcss-loader',
						options: {
							postcssOptions: {
								plugins: [
									Autoprefixer
								]
							}
						}
					},
					'sass-loader'
				]
			},

			// Rule: CSS
			{
				test: /\.css$/,
				use: [
					{
						loader: MiniCssExtractPlugin.loader
					},
					'css-loader',
				]
			},

			// Rule: TypeScript
			{
				test: /\.ts$/,
				use: 'ts-loader'
			}
		]
	},
	devServer: {
		hot: true,
		stats: 'none',
		contentBase: 'dist',
		watchContentBase: true
	},
	target: isDevelopment ? 'web' : 'browserslist',
	plugins: [
		new MiniCssExtractPlugin({
			filename: '[name].css'
		}),
		new Dotenv({
			safe: true, // load '.env.example' to verify the '.env' variables are all set. Can also be a string to a different file.
			systemvars: true // load all the predefined 'process.env' variables which will trump anything local per dotenv specs.
		})
	],
	devtool: isProduction && !sourceMapsInProduction ? false : 'source-map',
	stats: {
		chunks: false,
		chunkModules: false,
		modules: false,
		assets: true,
		entrypoints: false
	}
};

/**
 * This interface combines configuration from `webpack` and `webpack-dev-server`. You can add or override properties
 * in this interface to change the config object type used above.
 */
export interface Configuration extends Webpack.Configuration, WebpackDev.Configuration {

}

/*********************************************************************************************************************/
/**********                                             Advanced                                            **********/
/*********************************************************************************************************************/

// Configuration for production bundles
if (isProduction) {
	// Clean the build directory for production builds
	config.plugins?.push(new CleanWebpackPlugin());

	// Minify CSS files
	config.optimization?.minimizer?.push(
		new OptimizeCSSAssetsPlugin({
			cssProcessorOptions: {
				map: sourceMapsInProduction ? { inline: false, annotation: true, } : false,
			},
			cssProcessorPluginOptions: {
				preset: [
					'default',
					{
						discardComments: {
							removeAll: !sourceMapsInProduction,
						},
					},
				],
			},
		})
	);

	// Minify and treeshake JS
	if (config.optimization === undefined) {
		config.optimization = {};
	}

	config.optimization.minimize = true;
}

// Load path aliases from the tsconfig.json file
const tsconfigPath = path.resolve(__dirname, 'tsconfig.json');
const tsconfig = fs.existsSync(tsconfigPath) ? require(tsconfigPath) : {};

if ('compilerOptions' in tsconfig && 'paths' in tsconfig.compilerOptions) {
	const aliases = tsconfig.compilerOptions.paths;

	for (const alias in aliases) {
		const paths = aliases[alias].map((p: string) => path.resolve(__dirname, p));

		// Our tsconfig uses glob path formats, whereas webpack just wants directories
		// We'll need to transform the glob format into a format acceptable to webpack

		const wpAlias = alias.replace(/(\\|\/)\*$/, '');
		const wpPaths = paths.map((p: string) => p.replace(/(\\|\/)\*$/, ''));

		if (config.resolve && config.resolve.alias) {
			if (!(wpAlias in config.resolve.alias) && wpPaths.length) {
				config.resolve.alias[wpAlias] = wpPaths.length > 1 ? wpPaths : wpPaths[0];
			}
		}
	}
}

export default config;
