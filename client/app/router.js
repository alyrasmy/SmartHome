import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
	location: config.locationType
});

Router.map(function() {
	this.route('dashboard', {
		path: '/'
	}, function() {
		this.route('loading');
	});

	this.route('register');
	// this.route('team-details', {
	// 	path: '/teams/:team_name'
	// });

});

export
default Router;
