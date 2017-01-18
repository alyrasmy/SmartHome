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
  model: function(param) {
    let self = this;
    return Ember.RSVP.hash({
        user: self.get('store').find('user', self.controllerFor("application").get("username")),
        temperatures: self.get('store').find('temperature', {
          startDate: param.startDate,
          endDate: param.endDate
        }),
        humidities: self.get('store').find('humidity', {
          startDate: param.startDate,
          endDate: param.endDate
        }),
        leds: self.get('store').find('led', {
          startDate: param.startDate,
          endDate: param.endDate,
          boardId: param.roomId
        }),
        analyticType:param.analytic_type
      });
  },
  beforeModel: function() {
    if (!this.controllerFor("application").get("session.isAuthenticated")) {
        this.transitionTo("/");
    }
  }
});
