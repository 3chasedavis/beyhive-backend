import SwiftUI

struct TriviaQuestion: Identifiable {
    enum QuestionType {
        case multipleChoice, trueFalse, openEnded
    }
    let id = UUID()
    let question: String
    let type: QuestionType
    let options: [String]? // For multiple choice and true/false
    let correctAnswer: String
    let explanation: String?
}

struct DailyTriviaView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var currentIndex: Int = 0
    @State private var selectedAnswer: String? = nil
    @State private var showResult = false
    @State private var hasAnswered = false
    @State private var showPanel = false
    @State private var score = 0
    @State private var showFinal = false
    @State private var sessionQuestions: [TriviaQuestion] = DailyTriviaView.getSessionQuestions()
    @Namespace private var animation
    
    static let questions: [TriviaQuestion] = [
        TriviaQuestion(
            question: "What is Beyoncé’s full name?",
            type: .multipleChoice,
            options: [
                "Beyoncé Gabrielle Knowles-Carter",
                "Beyoncé Grace Knowles-Carter",
                "Beyoncé Georgette Knowles-Carter",
                "Beyoncé Giselle Knowles-Carter"
            ],
            correctAnswer: "Beyoncé Giselle Knowles-Carter",
            explanation: nil
        ),
        TriviaQuestion(
            question: "Beyoncé’s debut solo album was ‘Dangerously in Love.’",
            type: .trueFalse,
            options: ["True", "False"],
            correctAnswer: "True",
            explanation: nil
        ),
        TriviaQuestion(
            question: "What is the name of Beyoncé’s all-female tour band?",
            type: .openEnded,
            options: nil,
            correctAnswer: "Suga Mama",
            explanation: "In 2006, Beyoncé introduced her all-female tour band Suga Mama."
        ),
        TriviaQuestion(
            question: "In which year did Beyoncé release her debut solo album, Dangerously in Love?",
            type: .multipleChoice,
            options: ["2002", "2003", "2004", "2005"],
            correctAnswer: "2003",
            explanation: nil
        ),
        TriviaQuestion(
            question: "Beyoncé rose to fame in the late 1990s as the lead singer of what R&B girl-group?",
            type: .multipleChoice,
            options: ["The Spice Girls", "Destiny's Child", "TLC", "En Vogue"],
            correctAnswer: "Destiny's Child",
            explanation: nil
        ),
        TriviaQuestion(
            question: "Who did Beyoncé marry in 2008?",
            type: .multipleChoice,
            options: ["Jay-Z", "Ice Cube", "Kanye West", "Sean Combs"],
            correctAnswer: "Jay-Z",
            explanation: nil
        ),
        TriviaQuestion(
            question: "What is the name of Beyoncé’s alter ego?",
            type: .multipleChoice,
            options: ["Yoncé", "Sasha Fierce", "Queen Bey", "Mrs. Carter"],
            correctAnswer: "Sasha Fierce",
            explanation: nil
        ),
        TriviaQuestion(
            question: "True or False: Beyoncé made her big screen debut in Dreamgirls.",
            type: .trueFalse,
            options: ["True", "False"],
            correctAnswer: "False",
            explanation: "Her film debut was in Austin Powers in Goldmember."
        )
    ]
    
    static func getSessionQuestions() -> [TriviaQuestion] {
        // Pick 5 random questions for each session
        return questions.shuffled().prefix(5).map { $0 }
    }
    
    func resetGame() {
        currentIndex = 0
        selectedAnswer = nil
        showResult = false
        hasAnswered = false
        showPanel = false
        score = 0
        showFinal = false
        sessionQuestions = DailyTriviaView.getSessionQuestions()
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            withAnimation(.spring(response: 0.5, dampingFraction: 0.7)) {
                showPanel = true
            }
        }
    }
    
    var body: some View {
        ZStack {
            LinearGradient(
                gradient: Gradient(colors: [Color.red, Color.white, Color.blue]),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
            VStack {
                HStack {
                    Button(action: { dismiss() }) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(.black)
                            .padding(12)
                            .background(Color.white.opacity(0.7))
                            .clipShape(Circle())
                    }
                    Spacer()
                }
                .padding(.top, 32)
                .padding(.horizontal)
                Spacer(minLength: 0)
            }
            if showFinal {
                VStack(spacing: 24) {
                    Text("Trivia Complete!")
                        .ibmPlexMono(size: 32, weight: .bold)
                        .foregroundColor(.black)
                    Text("Score: \(score) / \(sessionQuestions.count)")
                        .ibmPlexMono(size: 24, weight: .regular)
                        .foregroundColor(.black)
                    Button(action: resetGame) {
                        Text("Play Again")
                            .ibmPlexMono(size: 20, weight: .bold)
                            .foregroundColor(.white)
                            .padding(.horizontal, 32)
                            .padding(.vertical, 14)
                            .background(Color.black)
                            .cornerRadius(16)
                    }
                }
                .frame(maxWidth: 400)
                .padding()
                .background(Color.white.opacity(0.95))
                .cornerRadius(32)
                .shadow(radius: 24)
                .transition(.scale.combined(with: .opacity))
            } else if currentIndex < sessionQuestions.count {
                let q = sessionQuestions[currentIndex]
                VStack(spacing: 0) {
                    Spacer(minLength: 60)
                    if showPanel {
                        VStack(spacing: 24) {
                            Text("Question \(currentIndex + 1) of \(sessionQuestions.count)")
                                .ibmPlexMono(size: 18, weight: .regular)
                                .foregroundColor(.gray)
                            Text(q.question)
                                .ibmPlexMono(size: 22, weight: .bold)
                                .foregroundColor(.black)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal)
                            if q.type == .multipleChoice || q.type == .trueFalse {
                                VStack(spacing: 12) {
                                    ForEach(q.options ?? [], id: \ .self) { option in
                                        Button(action: {
                                            if hasAnswered { return }
                                            selectedAnswer = option
                                            hasAnswered = true
                                            showResult = true
                                            if option.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() == q.correctAnswer.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() {
                                                score += 1
                                            }
                                            withAnimation(.easeInOut(duration: 0.5)) {
                                                showPanel = false
                                            }
                                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.55) {
                                                nextQuestion()
                                            }
                                        }) {
                                            HStack {
                                                Text(option)
                                                    .ibmPlexMono(size: 18, weight: .regular)
                                                    .foregroundColor(.black)
                                                Spacer()
                                            }
                                            .padding()
                                            .frame(maxWidth: .infinity)
                                            .background(
                                                Color.yellow.opacity(selectedAnswer == option ? 0.5 : 0.15)
                                            )
                                            .cornerRadius(12)
                                        }
                                        .disabled(hasAnswered)
                                    }
                                }
                                .padding(.horizontal)
                            } else if q.type == .openEnded {
                                VStack(spacing: 12) {
                                    TextField("Type your answer...", text: Binding(
                                        get: { selectedAnswer ?? "" },
                                        set: { selectedAnswer = $0 }
                                    ))
                                    .textFieldStyle(RoundedBorderTextFieldStyle())
                                    .ibmPlexMono(size: 18)
                                    .disabled(hasAnswered)
                                    Button("Submit") {
                                        hasAnswered = true
                                        showResult = true
                                        if selectedAnswer?.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() == q.correctAnswer.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() {
                                            score += 1
                                        }
                                        withAnimation(.easeInOut(duration: 0.5)) {
                                            showPanel = false
                                        }
                                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.55) {
                                            nextQuestion()
                                        }
                                    }
                                    .disabled(hasAnswered || (selectedAnswer ?? "").isEmpty)
                                }
                                .padding(.horizontal)
                            }
                            if showResult {
                                VStack(spacing: 8) {
                                    if selectedAnswer?.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() == q.correctAnswer.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() {
                                        Text("Correct!")
                                            .ibmPlexMono(size: 20, weight: .bold)
                                            .foregroundColor(.green)
                                    } else {
                                        Text("Incorrect")
                                            .ibmPlexMono(size: 20, weight: .bold)
                                            .foregroundColor(.red)
                                        Text("Correct answer: \(q.correctAnswer)")
                                            .ibmPlexMono(size: 16)
                                            .foregroundColor(.black)
                                    }
                                    if let explanation = q.explanation {
                                        Text(explanation)
                                            .ibmPlexMono(size: 14)
                                            .foregroundColor(.gray)
                                            .padding(.top, 4)
                                    }
                                }
                                .padding(.top, 8)
                            }
                        }
                        .frame(maxWidth: 400)
                        .padding()
                        .background(Color.white.opacity(0.98))
                        .cornerRadius(32)
                        .shadow(radius: 24)
                        .transition(.asymmetric(insertion: .scale.combined(with: .opacity), removal: .move(edge: .bottom).combined(with: .opacity)))
                        .padding(.horizontal, 16)
                        .matchedGeometryEffect(id: "panel", in: animation)
                    }
                    Spacer(minLength: 60)
                }
                .onAppear {
                    withAnimation(.spring(response: 0.5, dampingFraction: 0.7)) {
                        showPanel = true
                    }
                }
            }
        }
    }
    
    private func nextQuestion() {
        selectedAnswer = nil
        showResult = false
        hasAnswered = false
        if currentIndex + 1 < sessionQuestions.count {
            currentIndex += 1
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                withAnimation(.spring(response: 0.5, dampingFraction: 0.7)) {
                    showPanel = true
                }
            }
        } else {
            withAnimation(.easeInOut(duration: 0.5)) {
                showFinal = true
            }
        }
    }
} 