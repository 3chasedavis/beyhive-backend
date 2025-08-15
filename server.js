// Public events endpoint for iOS app
app.use('/api/events', eventsRoutes);

// Public partners endpoint for iOS app
app.use('/api/partners', partnersRouter);

// Security middleware
app.use(helmet());
