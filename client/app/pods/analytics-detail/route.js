import Ember from 'ember';

export default Ember.Route.extend({
  queryParams: {
      startDate: {
        refreshModel: true
      },
      endDate: {
        refreshModel: true
      },
      roomId: {
        refreshModel: true
      },
  },
  username: function(controller, model) {
    return controller.get('username');
  },
  model: function(param) {
    let self = this;
    return Ember.RSVP.hash({
        user: self.get('store').find('user', self.controllerFor("application").get("username")),
        temperatures: self.get('store').find('temperature'),
        humidities: self.get('store').find('humidity'),
        analyticType:param.analytic_type
      });
  },
});
