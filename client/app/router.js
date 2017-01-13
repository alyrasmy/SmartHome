import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
	location: config.locationType
});

Router.map(function() {
	this.route('dashboard', {
		path: '/welcome'
	}, function() {
		this.route('loading');
	});
	this.route('profile');
	this.route('register');
	this.route('newuser');
	this.route('control');

	this.route('analytics');
	this.route('analytics-detail', {
		path: '/analytics/:analytic_type'
	});

});

export
default Router;
